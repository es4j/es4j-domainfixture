package com.lingona.es4j.domain.persistence.core;

import com.lingona.es4j.api.*;
import com.lingona.es4j.api.domain.ISaga;
import com.lingona.es4j.api.domain.persistence.HeaderUpdater;
import com.lingona.es4j.api.domain.persistence.ISagaRepository;
import com.lingona.es4j.api.domain.persistence.PersistenceException;
import com.lingona.es4j.api.persistence.StorageException;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
//using System.Collections.Generic;
//using System.Linq;
//using global::EventStore;
//using global::EventStore.Persistence;


public class SagaEventStoreRepository implements ISagaRepository, Closeable {
    
    private static final String SagaTypeHeader            = "SagaType";
    private static final String UndispatchedMessageHeader = "UndispatchedMessage.";

    private final Map<UUID, IEventStream> streams         = new HashMap<UUID, IEventStream>();
    private final IStoreEvents            eventStore;

    public SagaEventStoreRepository(IStoreEvents eventStore) {
        this.eventStore = eventStore;
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

	synchronized (this.streams)
	{
	    for (Entry<UUID, IEventStream> entry : this.streams.entrySet()) {
		entry.getValue().close();
            }
            this.streams.clear();
        }
    }

    @Override
    public <TSaga extends ISaga> TSaga getById(UUID sagaId) { // where TSaga : class, ISaga, new()
        return buildSaga(this.openStream(sagaId));
    }
                
    private IEventStream openStream(UUID sagaId) {
        IEventStream stream;

        if(this.streams.containsKey(sagaId)) {
            return this.streams.get(sagaId);
        }

        try {
            stream = this.eventStore.openStream(sagaId, 0, Integer.MAX_VALUE);
        }
        catch (StreamNotFoundException ex) {
            stream = this.eventStore.createStream(sagaId);
        }

        this.streams.put(sagaId, stream);
        return stream;
    }

     static private <TSaga extends ISaga> TSaga buildSaga(IEventStream stream) { // where TSaga : class, ISaga, new()

        TSaga saga = new TSaga();
        for (Object event : stream.getCommittedEvents()/*.Select(x => x.Body)*/) {
            saga.transition(event);
        }

        saga.clearUncommittedEvents();
        saga.clearUndispatchedMessages();

        return saga;
    }

    @Override
    public void save(ISaga saga, UUID commitId, HeaderUpdater updateHeaders) {
        if (saga == null) {
            throw new IllegalArgumentException("saga", ExceptionMessages.NullArgument);
        }

        Map<String, Object> headers = prepareHeaders(saga, updateHeaders);
        IEventStream stream = this.prepareStream(saga, headers);

        persist(stream, commitId);

        saga.clearUncommittedEvents();
        saga.clearUndispatchedMessages();
    }

    private static Map<String, Object> prepareHeaders(ISaga saga, HeaderUpdater updateHeaders) {
        Map<String, Object> headers = new HashMap<String, Object>();

        headers.put(SagaTypeHeader, saga.getClass().getName());//.GetType().FullName;
        if (updateHeaders != null) {
            updateHeaders.updateHeader(headers);
        }

        int i = 0;
        for (Object command : saga.getUndispatchedMessages()) {
            headers.put(UndispatchedMessageHeader + i++, command);
        }

        return headers;
    }
            
    private IEventStream prepareStream(ISaga saga, Map<String, Object> headers) {
        IEventStream stream;

        UUID id = saga.getId();
        if(!this.streams.containsKey(id)) {
            stream = this.eventStore.createStream(id);
            this.streams.put(id, stream);
        }
        else {
            stream = this.streams.get(id);
        }

        for (Entry<String,Object> item : headers.entrySet()) {
            stream.getUncommittedHeaders().put(item.getKey(), item.getValue());
        }

        /*saga.GetUncommittedEvents()
				.Cast<object>()
				.Select(x => new EventMessage { Body = x })
				.ToList()
				.ForEach(stream.Add); */
        for(Object event : saga.getUncommittedEvents()) {
            EventMessage message = new EventMessage(event);
            stream.add(message);
        }

        return stream;
    }
 
    private static void persist(IEventStream stream, UUID commitId) {
        try {
            stream.commitChanges(commitId);
        } catch (DuplicateCommitException ex) {
            stream.clearChanges();
        } catch (StorageException e) {
            throw new PersistenceException(e.getMessage(), e);
        }
    }
}