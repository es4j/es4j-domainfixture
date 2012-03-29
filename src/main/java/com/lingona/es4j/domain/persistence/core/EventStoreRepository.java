package com.lingona.es4j.domain.persistence.core;

import com.lingona.es4j.api.*;
import com.lingona.es4j.api.domain.IAggregate;
import com.lingona.es4j.api.domain.IDetectConflicts;
import com.lingona.es4j.api.domain.IMemento;
import com.lingona.es4j.api.domain.persistence.*;
import com.lingona.es4j.api.persistence.StorageException;
import java.io.Closeable;
import java.util.Map.Entry;
import java.util.*;
//using System.Linq;
//using global::EventStore;
//using global::EventStore.Persistence;


public class EventStoreRepository implements IRepository, Closeable {

    private static final String AggregateTypeHeader = "AggregateType";
    
    private final Map<UUID, Snapshot>     snapshots = new HashMap<UUID, Snapshot>();
    private final Map<UUID, IEventStream> streams   = new HashMap<UUID, IEventStream>();
    private final IStoreEvents            eventStore;
    private final IConstructAggregates    factory;
    private final IDetectConflicts        conflictDetector;

    public EventStoreRepository(IStoreEvents          eventStore,
                                IConstructAggregates  factory,
                                IDetectConflicts      conflictDetector) {
        this.eventStore       = eventStore;
        this.factory          = factory;
        this.conflictDetector = conflictDetector;
    }

    @Override
    public void close() {
        this.dispose(true);
        //GC.suppressFinalize(this);
    }

    protected void dispose(boolean disposing) { // virtual
        if (!disposing) {
            return;
        }

	synchronized (this.streams) {
	    //foreach (var stream in this.streams) {
	    //    stream.Value.Dispose();
            //}
	    for (Entry<UUID, IEventStream> entry : this.streams.entrySet()) {
                entry.getValue().close();
            }
	    this.snapshots.clear();
	    this.streams  .clear();
	}
    }

    @Override
    public <TAggregate extends IAggregate> TAggregate getById(UUID id) { // where TAggregate : class, IAggregate // virtual
        return getById(id, Integer.MAX_VALUE);
    }

    @Override
    public <TAggregate extends IAggregate> TAggregate getById(UUID id, int versionToLoad) { // where TAggregate : class, IAggregate // virtual

        Snapshot     snapshot  = this.getSnapshot(id, versionToLoad);
        IEventStream stream    = this.openStream(id, versionToLoad, snapshot);
	TAggregate   aggregate = this.getAggregate(snapshot, stream);

        this.applyEventsToAggregate(versionToLoad, stream, aggregate);

        return aggregate; // as TAggregate;
    }
            
    private static void applyEventsToAggregate(int versionToLoad, IEventStream stream, IAggregate aggregate) {
        if (versionToLoad == 0 || aggregate.getVersion() < versionToLoad) {
            for (EventMessage event : stream.getCommittedEvents()/*.Select(x => x.Body)*/) {
                aggregate.applyEvent(event.getBody());
            }
        }
    }
                
    private <TAggregate extends IAggregate> TAggregate getAggregate(Snapshot snapshot, IEventStream stream) {
        IMemento memento = null;
        if(snapshot != null) {
            if(snapshot.getPayload() instanceof IMemento) {
                memento = (IMemento)snapshot.getPayload();
            }
            else {
                throw new RuntimeException();
            }
        }
        return this.factory.build(typeof(TAggregate), stream.getStreamId(), memento);
    }
                
    private Snapshot getSnapshot(UUID id, int version) {
        Snapshot snapshot;
        if(this.snapshots.containsKey(id)) {
            snapshot = this.snapshots.get(id);
        }
        else {
            snapshot = this.eventStore.advanced().getSnapshot(id, version);
            this.snapshots.put(id, snapshot);
        }
        return snapshot;
    }
                
    private IEventStream openStream(UUID id, int version, Snapshot snapshot) {
        IEventStream stream;
        if (this.streams.containsKey(id)) {
	    return this.streams.get(id);
        }

        stream = (snapshot == null)? this.eventStore.openStream(id, 0, version)
				   : this.eventStore.openStream(snapshot, version);

        this.streams.put(id, stream);
        return stream;
    }

    @Override
    public void save(IAggregate aggregate, UUID commitId, HeaderUpdater updateHeaders) { // virtual
        Map<String, Object> headers = prepareHeaders(aggregate, updateHeaders);
        while (true) {
            IEventStream stream = this.prepareStream(aggregate, headers);
            int commitEventCount = stream.getCommittedEvents().size();

            try {
                stream.commitChanges(commitId);
                aggregate.clearUncommittedEvents();
                return;
            }
            catch (DuplicateCommitException ex) {
                stream.clearChanges();
                return;
            }
            catch (ConcurrencyException e) {
                if (this.throwOnConflict(stream, commitEventCount)) {
                    throw new ConflictingCommandException(e.getMessage(), e);
                }

                stream.clearChanges();
            }
            catch (StorageException e) {
                throw new PersistenceException(e.getMessage(), e);
            }
        }
    }
                
    private IEventStream prepareStream(IAggregate aggregate, Map<String, Object> headers) {
        IEventStream stream;
        UUID id = aggregate.getId();
        if(!this.streams.containsKey(id)) {
            stream = this.eventStore.createStream(null);
            this.streams.put(id, stream);
        }
        else {
            stream = this.streams.get(id);
        }
            
        for (Entry<String, Object> item : headers.entrySet()) {
            stream.getUncommittedHeaders().put(item.getKey(), item.getValue());
        }

        /*aggregate.GetUncommittedEvents()
				.Cast<object>()
				.Select(x => new EventMessage { Body = x })
				.ToList()
				.ForEach(stream.Add); */
        for(Object object : aggregate.getUncommittedEvents()) {
            EventMessage message = new EventMessage(object);
            stream.add(message);
        }

        return stream;
    }

    private static Map<String, Object> prepareHeaders(IAggregate aggregate, HeaderUpdater updateHeaders) {

        Map<String, Object> headers = new HashMap<String, Object>();

        headers.put(AggregateTypeHeader, aggregate.getClass().getName());
        if (updateHeaders != null) {
            updateHeaders.updateHeader(headers);
        }
        return headers;
    }
                
    private boolean throwOnConflict(IEventStream stream, int skip) {
        List<Object> committed = new LinkedList<Object>();
        int idx = skip;
        for(EventMessage event : stream.getCommittedEvents()) {
            if(idx > 0) {
                idx--;
                continue;
            }
            committed.add(event.getBody());
        }

        List<Object> uncommitted = new LinkedList<Object>(); // = stream.getUncommittedEvents(); //.Select(x => x.Body);
        for(EventMessage event : stream.getUncommittedEvents()) {
            committed.add(event.getBody());
        }

        return this.conflictDetector.conflictsWith(uncommitted, committed);
    }
	
}