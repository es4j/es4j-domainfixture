package com.lingona.es4j.domain.core;

import com.lingona.es4j.domain.api.EventHandlerDelegate;
import com.lingona.es4j.domain.api.ISaga;
import java.util.*;

//using System.Collections;
//using System.Collections.Generic;

public abstract class SagaBase<TMessage> implements ISaga/*, IEquatable<ISaga>*/ { // where TMessage : class

    private final Map<Class, EventHandlerDelegate<TMessage>> handlers = new HashMap<Class, EventHandlerDelegate<TMessage>>();
    private final Collection<TMessage>               uncommitted  = new LinkedList<TMessage>();
    private final Collection<TMessage>               undispatched = new LinkedList<TMessage>();

    private UUID id;      // { get; protected set; }
    private int  version; // { get; private set; }

    protected <TRegisteredMessage> void register(EventHandlerDelegate<TRegisteredMessage> handler) { // where TRegisteredMessage : class, TMessage
	throw new UnsupportedOperationException("Not yet implemented");
        //this.handlers[typeof(TRegisteredMessage)] = message => handler(message as TRegisteredMessage);
    }

    @Override
    public void transition(Object message) {
        throw new UnsupportedOperationException("Not yet implemented");
        //this.handlers[message.getClass()](message as TMessage);
        //this.uncommitted.add(message as TMessage);
        //this.version++;
    }

    @Override
    public Collection /*ISaga.*/getUncommittedEvents() {
        return this.uncommitted; // as ICollection;
    }

    @Override
    public void /*ISaga.*/clearUncommittedEvents() {
        this.uncommitted.clear();
    }

    protected void dispatch(TMessage message) {
        this.undispatched.add(message);
    }

    @Override
    public Collection /*ISaga.*/getUndispatchedMessages() {
        return this.undispatched; // as ICollection;
    }

    @Override
    public void /*ISaga.*/clearUndispatchedMessages() {
        this.undispatched.clear();
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ISaga)? this.equals((ISaga)obj) : false;
    }

    public boolean equals(ISaga other) // virtual
    {
        return null != other && other.getId() == this.id;
    }
}