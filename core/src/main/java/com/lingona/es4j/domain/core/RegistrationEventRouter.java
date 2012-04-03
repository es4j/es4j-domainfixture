package com.lingona.es4j.domain.core;

import com.lingona.es4j.domain.api.EventHandlerDelegate;
import com.lingona.es4j.domain.api.IAggregate;
import com.lingona.es4j.domain.api.IRouteEvents;
import java.util.HashMap;
import java.util.Map;


public class RegistrationEventRouter<TEvent> implements IRouteEvents<TEvent> {

    private final Map<Class, EventHandlerDelegate<TEvent>> handlers = new HashMap<Class, EventHandlerDelegate<TEvent>>();
    private IAggregate regsitered;

    @Override
    public <TEventMessage extends TEvent> void register(EventHandlerDelegate<TEventMessage> eventHandler) {
        this.handlers.put(eventHandler.getEventClass(), (EventHandlerDelegate<TEvent>)eventHandler);
    }

    @Override
    public void register(IAggregate aggregate) { // virtual
        if (aggregate == null) {
            throw new IllegalArgumentException("aggregate can not be null");
        }
        this.regsitered = aggregate;
    }

    @Override
    public <TEventMessage extends TEvent> void dispatch(TEventMessage eventMessage) { // virtual

        if(!this.handlers.containsKey(eventMessage.getClass())) {
            this.regsitered.throwHandlerNotFound(eventMessage);
        }
        EventHandlerDelegate<TEventMessage> handler = (EventHandlerDelegate<TEventMessage>) this.handlers.get(eventMessage.getClass());

        handler.handleEvent(eventMessage);
    }
}
