package com.lingona.cd4j.persistence.api;

import com.lingona.cd4j.api.IAggregate;
import com.lingona.cd4j.api.IMemento;
import java.util.UUID;


public interface IConstructAggregates {

    IAggregate Build(Class type, UUID id, IMemento snapshot);
}
