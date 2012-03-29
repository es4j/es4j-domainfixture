package com.lingona.es4j.domain.persistence.api;


import com.lingona.es4j.api.domain.IAggregate;
import com.lingona.es4j.api.domain.persistence.IIdentityMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ThreadedIdentityMap implements IIdentityMap {
    
    private final Map<UUID, Object>     locks = new HashMap<UUID, Object>();
    private final Map<UUID, IAggregate> map   = new HashMap<UUID, IAggregate>();

    public IAggregate getById(UUID id) {
        IAggregate aggregate = null;
        synchronized (this.obtainLock(id)) {
            if(this.map.containsKey(id)) {
                aggregate = this.map.get(id);
            }
        }
	return aggregate;
    }

    public void add(IAggregate aggregate) {
        UUID id = aggregate.getId();
        synchronized (this.obtainLock(id)) {
            this.map.put(id, aggregate);
        }
    }

    public void eject(UUID id) {
        synchronized (this.obtainLock(id)) {
            this.map.remove(id);
        }
    }

    private Object obtainLock(UUID id) {
        if(this.locks.containsKey(id)) {
            return this.locks.get(id);
        }

        Object lock;
	synchronized (this.locks) {
            if(!this.locks.containsKey(id)) {
                lock = new Object();
                this.locks.put(id, lock);
            }
            else {
                lock = this.locks.get(id);
            }
        }
        return lock;
    }
}