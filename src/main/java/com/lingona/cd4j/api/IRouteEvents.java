package com.lingona.cd4j.api;


public interface IRouteEvents {

    <T> void register(EventHandler<T> handler);

    void register(IAggregate aggregate);

    void dispatch(Object eventMessage);
}
