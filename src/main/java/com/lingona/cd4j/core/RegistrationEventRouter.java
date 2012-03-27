package com.lingona.cd4j.core;

import com.lingona.cd4j.api.EventHandler;
import com.lingona.cd4j.api.IAggregate;
import com.lingona.cd4j.api.IRouteEvents;
import java.util.HashMap;
import java.util.Map;


public class RegistrationEventRouter implements IRouteEvents {

    private final Map<Class, EventHandler<Object>> handlers = new HashMap<Class, EventHandler<Object>>();
    private IAggregate regsitered;

    public <T> void register(EventHandler<T> handler) { // virtual
        

        this.handlers.put(Class<T>, event => handler((T)event);
    }

    public void register(IAggregate aggregate) { // virtual
        if (aggregate == null) {
            throw new IllegalArgumentException("aggregate can not be null");
        }
        this.regsitered = aggregate;
    }

    public void dispatch(Object eventMessage) { // virtual
        EventHandler<Object> handler;

        if (!this.handlers.TryGetValue(eventMessage.GetType(), out handler)) {
            this.regsitered.throwHandlerNotFound(eventMessage);
        }
        
        if(!this.handlers.containsKey(eventMessage.getClass())) {
            this.regsitered.throwHandlerNotFound(eventMessage);
        }
        handler = this.handlers.get(eventMessage.getClass());

        handler.handleEvent(eventMessage);
    }
}
