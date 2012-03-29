package com.lingona.es4j.domain.persistence.api;

import com.lingona.es4j.api.domain.IAggregate;
import java.util.UUID;


public interface IRepository {
    public <TAggregate extends IAggregate> TAggregate getById(UUID id); // where TAggregate : class, IAggregate;

    <TAggregate extends IAggregate> TAggregate getById(UUID id, int version); // where TAggregate : class, IAggregate;

    void save(IAggregate aggregate, UUID commitId, HeaderUpdater headerUpdater/*updateHeaders*/);
}
