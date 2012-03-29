package com.lingona.es4j.domain.persistence.api;

import com.lingona.es4j.api.domain.IAggregate;
import com.lingona.es4j.api.domain.IMemento;
import java.util.UUID;


public interface IConstructAggregates {

    IAggregate Build(Class type, UUID id, IMemento snapshot);
}
