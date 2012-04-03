package com.lingona.es4j.domain.core;


import com.lingona.es4j.domain.api.EventHandlerDelegate;
import com.lingona.es4j.domain.api.IAggregate;
import com.lingona.es4j.domain.api.IRouteEvents;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
//using System.Reflection;


public class AnnotatedEventRouter<TEvent> implements IRouteEvents<TEvent> {

    private final boolean throwOnApplyNotFound;
    private final Map<Class, EventHandlerDelegate<TEvent>> handlers = new HashMap<Class, EventHandlerDelegate<TEvent>>();
    private IAggregate registered;

    public AnnotatedEventRouter() {
        this(true);
    }

    public AnnotatedEventRouter(boolean throwOnApplyNotFound) {
        this.throwOnApplyNotFound = throwOnApplyNotFound;
    }

    public AnnotatedEventRouter(boolean throwOnApplyNotFound, IAggregate aggregate) {
        this(throwOnApplyNotFound);
        register(aggregate);
    }

    @Override
    public <TEventMessage extends TEvent> void register(EventHandlerDelegate<TEventMessage> handler) { //virtual
        if (handler == null) {
            throw new IllegalArgumentException("handler");
        }
        throw new UnsupportedOperationException("Not yet implemented");
        //EventHandler h = new EventHandler(handler) {
        //    @Override
        //    public void handleEvent(Object eventMessage) {
        //        execute(eventMessage); //handler((T)event);
        //    }
        //};
        
        //TEventMessage eventMessage = null;
	//this.register(eventClass, handler/*event => handler((T)event)*/);
    }

    //@Override
    //public <TEventMessage extends TEvent> void register(Class<TEventMessage> eventClass, EventHandler<TEventMessage> eventHandler) {
    //    this.handlers.put(eventClass, (EventHandler<TEvent>)eventHandler);
    //}

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
                EventHandlerDelegate handler = new EventHandlerDelegate(messageType) {
                    @Override
                    public void handleEvent(Object eventMessage) {
                        throw new UnsupportedOperationException("Not yet implemented");
                        //executeApplyOnAggregate(eventMessage);
                    }
                };
                this.handlers.put(messageType, handler);
            }
        }
    }
    
    @Override
    public <TEventMessage extends TEvent> void dispatch(TEventMessage eventMessage) { // virtual
        if (eventMessage == null) {
	    throw new IllegalArgumentException("eventMessage");
        }
                        
        if(this.handlers.containsKey(eventMessage.getClass())) {
           EventHandlerDelegate<TEventMessage> handler = (EventHandlerDelegate<TEventMessage>) this.handlers.get(eventMessage.getClass());
           throw new UnsupportedOperationException("Not yet implemented");
           //handler.executeApplyOnAggregate(eventMessage);
        }
        else if(this.throwOnApplyNotFound) {
            ExtensionMethods.throwHandlerNotFound(this.registered, eventMessage);
        }
    }

}
