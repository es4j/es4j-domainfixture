package com.lingona.cd4j.core;

//using System;

import com.lingona.cd4j.api.EventHandler;
import com.lingona.cd4j.api.ISaga;
import java.util.*;

//using System.Collections;
//using System.Collections.Generic;

public class SagaBase<TMessage> implements ISaga/*, IEquatable<ISaga>*/ { // where TMessage : class
	
    private final Map<Class, EventHandler<TMessage>> handlers = new HashMap<Class, EventHandler<TMessage>>();
    private final Collection<TMessage>               uncommitted  = new LinkedList<TMessage>();
    private final Collection<TMessage>               undispatched = new LinkedList<TMessage>();

    private UUID id;      // { get; protected set; }
    private int  version; // { get; private set; }

    protected <TRegisteredMessage> void register(EventHandler<TRegisteredMessage> handler) { // where TRegisteredMessage : class, TMessage
		
        this.handlers[typeof(TRegisteredMessage)] = message => handler(message as TRegisteredMessage);
    }

		public void transition(Object message) {
			this.handlers[message.getClass()](message as TMessage);
			this.uncommitted.Add(message as TMessage);
			this.Version++;
		}
                
		Collection ISaga.getUncommittedEvents() {
			return this.uncommitted as ICollection;
		}
                
		void ISaga.clearUncommittedEvents() {
			this.uncommitted.Clear();
		}

		protected void Dispatch(TMessage message)
		{
			this.undispatched.Add(message);
		}
                
		Collection ISaga.GetUndispatchedMessages()
		{
			return this.undispatched as ICollection;
		}
                
		void ISaga.ClearUndispatchedMessages()
		{
			this.undispatched.Clear();
		}

                @Override
		public int hashCode()
		{
			return this.Id.GetHashCode();
		}
                
                @Override
		public boolean equals(Object obj)
		{
			return this.Equals(obj as ISaga);
		}
                
		public boolean equals(ISaga other) // virtual
		{
			return null != other && other.Id == this.Id;
		}
	
}