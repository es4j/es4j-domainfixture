package com.lingona.cd4j.persistence.es4j;

import com.lingona.cd4j.api.ISaga;
import com.lingona.cd4j.persistence.api.ISagaRepository;
import com.lingona.cd4j.persistence.api.PersistenceException;
import java.awt.Desktop.Action;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import sun.misc.GC;

	using System;
	using System.Collections.Generic;
	using System.Linq;
	using global::EventStore;
	using global::EventStore.Persistence;

public class SagaEventStoreRepository implements ISagaRepository, IDisposable {
    
    private static final String SagaTypeHeader = "SagaType";
    private static final String UndispatchedMessageHeader = "UndispatchedMessage.";
    
    private final Map<UUID, IEventStream> streams     = new HashMap<UUID, IEventStream>();
    private final IStoreEvents            eventStore;

    public SagaEventStoreRepository(IStoreEvents eventStore) {
        this.eventStore = eventStore;
    }

    public void dispose() {
        this.Dispose(true);
        GC.suppressFinalize(this);
    }

    protected void dispose(boolean disposing) { // virtual
        if (!disposing) {
            return;
        }

	lock (this.streams)
	{
	    for (var stream in this.streams) {
		stream.Value.Dispose();
            }
            this.streams.clear();
        }
    }

    public TSaga getById<TSaga>(UUID sagaId) where TSaga : class, ISaga, new() 
    {
        return BuildSaga<TSaga>(this.OpenStream(sagaId));
    }
                
    private IEventStream openStream(UUID sagaId) {
			IEventStream stream;
			if (this.streams.TryGetValue(sagaId, out stream))
				return stream;

			try {
				stream = this.eventStore.OpenStream(sagaId, 0, int.MaxValue);
			}
			catch (StreamNotFoundException)
			{
				stream = this.eventStore.CreateStream(sagaId);
			}

			return this.streams[sagaId] = stream;
    }

    private static TSaga BuildSaga<TSaga>(IEventStream stream) where TSaga : class, ISaga, new()
    {
			var saga = new TSaga();
			foreach (var @event in stream.CommittedEvents.Select(x => x.Body))
				saga.Transition(@event);

			saga.ClearUncommittedEvents();
			saga.ClearUndispatchedMessages();

			return saga;
    }

    public void Save(ISaga saga, UUID commitId, Action<Map<String, Oject>> updateHeaders) {
			if (saga == null) {
				throw new ArgumentNullException("saga", ExceptionMessages.NullArgument);
                        }

			var headers = PrepareHeaders(saga, updateHeaders);
			var stream = this.PrepareStream(saga, headers);

			Persist(stream, commitId);

			saga.ClearUncommittedEvents();
			saga.ClearUndispatchedMessages();
    }
                
    private static Map<String, Object> prepareHeaders(ISaga saga, Action<Map<String, Object>> updateHeaders) {
			var headers = new Dictionary<string, object>();

			headers[SagaTypeHeader] = saga.GetType().FullName;
			if (updateHeaders != null)
				updateHeaders(headers);

			var i = 0;
			foreach (var command in saga.GetUndispatchedMessages())
				headers[UndispatchedMessageHeader + i++] = command;

			return headers;
    }
                
    private IEventStream prepareStream(ISaga saga, Map<String, Object> headers) {
			IEventStream stream;
			if (!this.streams.TryGetValue(saga.Id, out stream))
				this.streams[saga.Id] = stream = this.eventStore.CreateStream(saga.Id);

			foreach (var item in headers)
				stream.UncommittedHeaders[item.Key] = item.Value;

			saga.GetUncommittedEvents()
				.Cast<object>()
				.Select(x => new EventMessage { Body = x })
				.ToList()
				.ForEach(stream.Add);

			return stream;
    }
                
    private static void persist(IEventStream stream, UUID commitId) {
			try {
				stream.CommitChanges(commitId);
			}
			catch (DuplicateCommitException) {
				stream.ClearChanges();
			}
			catch (StorageException e) {
				throw new PersistenceException(e.Message, e);
			}
    }

}