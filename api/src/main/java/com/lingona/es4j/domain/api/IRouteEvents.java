package com.lingona.es4j.domain.api;


public interface IRouteEvents<TEvent> {

    <TEventMessage extends TEvent> void register(EventHandlerDelegate<TEventMessage> eventHandler);
    void register(IAggregate aggregate);

    <TEventMessage extends TEvent>
    void dispatch(TEventMessage eventMessage);
}


/*
public interface IRouteEvents<TEvent>
{
	void Register<TEventMessage>(Action<TEventMessage> handler) where TEventMessage : TEvent;
	void Register(IAggregate aggregate);

	void Dispatch(object eventMessage);
}
*/