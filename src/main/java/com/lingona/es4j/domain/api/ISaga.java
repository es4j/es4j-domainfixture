package com.lingona.es4j.domain.api;

import java.util.Collection;
import java.util.UUID;


public interface ISaga {

    UUID getId();     // { get; }
    int gitVersion(); // { get; }

    void transition(Object message);

    Collection   getUncommittedEvents();
    void       clearUncommittedEvents();

    Collection   getUndispatchedMessages();
    void       clearUndispatchedMessages();
}