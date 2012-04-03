package com.lingona.es4j.domain.core;

import com.lingona.es4j.domain.api.EventHandlerDelegate;
import com.lingona.es4j.domain.api.IAggregate;
import com.lingona.es4j.domain.api.IRouteEvents;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
//using System.Reflection;
//using System.Linq;


public class ConventionEventRouter<TEvent> implements IRouteEvents<TEvent> {

    private final boolean throwOnApplyNotFound;
    private final Map<Class, EventHandlerDelegate> handlers = new HashMap<Class, EventHandlerDelegate>();
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
    public <TEventMessage extends TEvent> void register(EventHandlerDelegate<TEventMessage> handler) { //virtual
        if (handler == null) {
            throw new IllegalArgumentException("handler");
        }
        
        //EventHandler h = new EventHandler(handler) {
        //    @Override
        //    public void handleEvent(Object eventMessage) {
        //        execute(eventMessage); //handler((T)event);
        //    }
        //};
        
	//this.register(event => handler((T)event));
        this.handlers.put(handler.getEventClass(), handler);
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
                /*
                EventHandlerInvoker handler = new EventHandlerInvoker(aggregate, method) {
                    @Override
                    public void handleEvent(Object eventMessage) {
                        executeApplyOnAggregate(eventMessage);
                    }
                };
                this.handlers.put(messageType, handler);
                */
            }
        }
    }
    
    @Override
    public void dispatch(Object eventMessage) { // virtual
        if (eventMessage == null) {
	    throw new IllegalArgumentException("eventMessage");
        }
                        
        if(this.handlers.containsKey(eventMessage.getClass())) {
           EventHandlerDelegate<Object> handler = this.handlers.get(eventMessage.getClass());
           //handler.executeApplyOnAggregate(eventMessage);
           handler.handleEvent(eventMessage);
        }
        else if(this.throwOnApplyNotFound) {
            ExtensionMethods.throwHandlerNotFound(this.registered, eventMessage);
        }
    }

}
