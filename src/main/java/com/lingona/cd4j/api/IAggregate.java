package com.lingona.cd4j.api;

import java.util.Collection;
import java.util.UUID;


public interface IAggregate {

    UUID getId();      // { get; }
    int  getVersion(); // { get; }

    void applyEvent(Object event);

    Collection   getUncommittedEvents();
    void       clearUncommittedEvents();

    IMemento getSnapshot();
}