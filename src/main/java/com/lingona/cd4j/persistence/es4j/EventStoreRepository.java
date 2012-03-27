package com.lingona.cd4j.persistence.es4j;

import com.lingona.cd4j.api.IAggregate;
import com.lingona.cd4j.api.IDetectConflicts;
import com.lingona.cd4j.persistence.api.ConflictingCommandException;
import com.lingona.cd4j.persistence.api.IConstructAggregates;
import com.lingona.cd4j.persistence.api.IRepository;
import com.lingona.cd4j.persistence.api.PersistenceException;
import com.lingona.es4j.api.*;
import com.lingona.es4j.api.persistence.StorageException;
import java.awt.Desktop.Action;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

	lock (this.streams)
	{
	    foreach (var stream in this.streams) {
	        stream.Value.Dispose();
            }
	    this.snapshots.Clear();
	    this.streams.Clear();
	}
    }

    public <TAggregate> TAggregate getById(UUID id) { // where TAggregate : class, IAggregate // virtual
        return GetById<TAggregate>(id, int.MaxValue);
    }

	    public virtual TAggregate GetById<TAggregate>(Guid id, int versionToLoad) where TAggregate : class, IAggregate
		{
			var snapshot = this.GetSnapshot(id, versionToLoad);
			var stream = this.OpenStream(id, versionToLoad, snapshot);
			var aggregate = this.GetAggregate<TAggregate>(snapshot, stream);

			ApplyEventsToAggregate(versionToLoad, stream, aggregate);

			return aggregate as TAggregate;
		}
            
		private static void ApplyEventsToAggregate(int versionToLoad, IEventStream stream, IAggregate aggregate)
		{
			if (versionToLoad == 0 || aggregate.Version < versionToLoad)
				foreach (var @event in stream.CommittedEvents.Select(x => x.Body))
					aggregate.ApplyEvent(@event);
		}
                
		private IAggregate GetAggregate<TAggregate>(Snapshot snapshot, IEventStream stream)
		{
			var memento = snapshot == null ? null : snapshot.Payload as IMemento;
			return this.factory.Build(typeof(TAggregate), stream.StreamId, memento);
		}
                
		private Snapshot GetSnapshot(Guid id, int version)
		{
			Snapshot snapshot;
			if (!this.snapshots.TryGetValue(id, out snapshot))
				this.snapshots[id] = snapshot = this.eventStore.Advanced.GetSnapshot(id, version);

			return snapshot;
		}
                
		private IEventStream OpenStream(Guid id, int version, Snapshot snapshot)
		{
			IEventStream stream;
			if (this.streams.TryGetValue(id, out stream))
				return stream;

			stream = snapshot == null
				? this.eventStore.OpenStream(id, 0, version)
				: this.eventStore.OpenStream(snapshot, version);

			return this.streams[id] = stream;
		}

		public virtual void Save(IAggregate aggregate, Guid commitId, Action<IDictionary<string, object>> updateHeaders)
		{
			var headers = PrepareHeaders(aggregate, updateHeaders);
			while (true)
			{
				var stream = this.PrepareStream(aggregate, headers);
				var commitEventCount = stream.CommittedEvents.Count;

				try
				{
					stream.CommitChanges(commitId);
					aggregate.ClearUncommittedEvents();
					return;
				}
				catch (DuplicateCommitException)
				{
					stream.ClearChanges();
					return;
				}
				catch (ConcurrencyException e)
				{
					if (this.ThrowOnConflict(stream, commitEventCount))
						throw new ConflictingCommandException(e.Message, e);

					stream.ClearChanges();
				}
				catch (StorageException e)
				{
					throw new PersistenceException(e.Message, e);
				}
			}
		}
                
		private IEventStream prepareStream(IAggregate aggregate, Map<String, Object> headers)
		{
			IEventStream stream;
			if (!this.streams.TryGetValue(aggregate.Id, out stream))
				this.streams[aggregate.Id] = stream = this.eventStore.CreateStream(aggregate.Id);

			foreach (var item in headers)
				stream.UncommittedHeaders[item.Key] = item.Value;

			aggregate.GetUncommittedEvents()
				.Cast<object>()
				.Select(x => new EventMessage { Body = x })
				.ToList()
				.ForEach(stream.Add);

			return stream;
		}
                
		private static Map<String, Object> prepareHeaders(IAggregate aggregate, Action<Map<String, Object>> updateHeaders)
		{
			Map<String, Object> headers = new HashMap<String, Object>();

			headers[AggregateTypeHeader] = aggregate.GetType().FullName;
			if (updateHeaders != null) {
				updateHeaders(headers);
                        }
			return headers;
		}
                
		private boolean throwOnConflict(IEventStream stream, int skip)
		{
			var committed = stream.CommittedEvents.Skip(skip).Select(x => x.Body);
			var uncommitted = stream.UncommittedEvents.Select(x => x.Body);
			return this.conflictDetector.ConflictsWith(uncommitted, committed);
		}
	
}