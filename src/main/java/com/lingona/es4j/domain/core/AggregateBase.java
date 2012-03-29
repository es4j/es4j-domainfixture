package com.lingona.es4j.domain.core;


import com.lingona.es4j.api.domain.EventHandler;
import com.lingona.es4j.api.domain.IAggregate;
import com.lingona.es4j.api.domain.IMemento;
import com.lingona.es4j.api.domain.IRouteEvents;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;


public abstract class AggregateBase implements IAggregate, IEquatable<IAggregate>  {
    
        private final Collection<Object> uncommittedEvents = new LinkedList<Object>();

        private IRouteEvents registeredRoutes;
        
        private UUID       id;      // { get; protected set; }
        private int        version; // { get; protected set; }
        protected IMemento snapshot;

 
        protected AggregateBase() {
            this(null);
        }

        protected AggregateBase(IRouteEvents handler)
        {
            if (handler == null) return;

            this.registeredRoutes = handler;
            this.registeredRoutes.register(this);
        }

        //protected IRouteEvents registeredRoutes;
        public IRouteEvents getRegisteredRoutes() {
            
            if(registeredRoutes == null) {
                registeredRoutes = new ConventionEventRouter(true, this);
            }
            return registeredRoutes;
        }
            
        public void setRegisteredRoutes(IRouteEvents registeredRoutes) {
            
                if (registeredRoutes == null) {
                    throw new NullPointerException("AggregateBase must have an event router to function");
                }
                this.registeredRoutes = registeredRoutes;
        }
        

        protected <T> void register(EventHandler<T> route)
        {
            this.registeredRoutes.register(route);
        }

        protected void raiseEvent(Object event)
        {
            ((IAggregate)this).applyEvent(event);
            this.uncommittedEvents.add(event);
        }
        
        @Override
        public void /*IAggregate.*/applyEvent(Object event)
        {
            this.registeredRoutes.dispatch(event);
            this.version++;
        }
        
        @Override
        public Collection /*IAggregate.*/getUncommittedEvents()
        {
            return (Collection)this.uncommittedEvents;
        }
        
        @Override
        public void /*IAggregate.*/clearUncommittedEvents()
        {
            this.uncommittedEvents.clear();
        }

        @Override
        public IMemento /*IAggregate.*/getSnapshot()
        {
            this.snapshot.setId(this.getId());
            this.snapshot.setVersion(this.getVersion());
            return snapshot;
        }
        /*
        protected IMemento getSnapshot() { // virtual
            return null;
        }
        */
        protected void setSnapshot(IMemento snapshot) {
            this.snapshot = snapshot;
        }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IAggregate) {
            return this.equals((IAggregate) obj);
        }
        else {
            return false;
        }
    }

    public boolean equals(IAggregate other) { //virtual
        return null != other && other.getId() == this.id;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public int getVersion() {
        return version;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

}
