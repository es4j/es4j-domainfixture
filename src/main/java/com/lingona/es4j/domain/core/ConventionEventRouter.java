package com.lingona.es4j.domain.core;

import com.lingona.es4j.api.domain.EventHandler;
import com.lingona.es4j.api.domain.IAggregate;
import com.lingona.es4j.api.domain.IRouteEvents;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
//using System.Reflection;
//using System.Linq;


public class ConventionEventRouter implements IRouteEvents {

    private final boolean throwOnApplyNotFound;
    private final Map<Class, EventHandler<Object>> handlers = new HashMap<Class, EventHandler<Object>>();
    private IAggregate registered;

    public ConventionEventRouter() {
        this(true);
    }

    public ConventionEventRouter(boolean throwOnApplyNotFound) {
        this.throwOnApplyNotFound = throwOnApplyNotFound;
    }

    public ConventionEventRouter(boolean throwOnApplyNotFound, IAggregate aggregate) {
        this(throwOnApplyNotFound);
        register(aggregate);
    }

    @Override
    public <T> void register(EventHandler<T> handler) { //virtual
        if (handler == null) {
            throw new IllegalArgumentException("handler");
        }
        
        EventHandler h = new EventHandler(handler) {
            @Override
            public void handleEvent(Object eventMessage) {
                execute(eventMessage); //handler((T)event);
            }
        };
        
        T eventMessage = null;
	this.register(eventMessage.getClass(), h/*event => handler((T)event)*/);
    }

    @Override
    public void register(IAggregate aggregate) { // virutal
        if (aggregate == null) {
	    throw new IllegalArgumentException("aggregate");
        }

	this.registered = aggregate;

        // Get instance methods named Apply with one parameter returning void
        for(Method method : aggregate.getClass().getMethods()) {
            if("action".equals(method.getName()) && 
               Void.TYPE.equals(java.lang.Void.TYPE) &&
               1 == method.getParameterTypes().length) 
            {
                Class messageType = method.getParameterTypes()[0];                    
                EventHandler handler = new EventHandler(aggregate, method) {
                    @Override
                    public void handleEvent(Object eventMessage) {
                        executeApplyOnAggregate(eventMessage);
                    }
                };
                this.handlers.put(messageType, handler);
            }
        }
    }
    
    public void dispatch(Object eventMessage) { // virtual
        if (eventMessage == null) {
	    throw new IllegalArgumentException("eventMessage");
        }
                        
        if(this.handlers.containsKey(eventMessage.getClass())) {
           EventHandler<Object> handler = this.handlers.get(eventMessage.getClass());
            handler.executeApplyOnAggregate(eventMessage);    
        }
        else if(this.throwOnApplyNotFound) {
            ExtensionMethods.throwHandlerNotFound(this.registered, eventMessage);
        }
    }

    private void register(Class messageType, EventHandler<Object> handler) {
        this.handlers.put(messageType, handler);
    }
}
