package com.lingona.es4j.domain.persistence.api;

import com.lingona.es4j.api.domain.ISaga;
import java.util.UUID;

public interface ISagaRepository {

    <TSaga extends ISaga> TSaga getById(UUID sagaId); // where TSaga : class, ISaga, new();

    void Save(ISaga saga, UUID commitId, HeaderUpdater updateHeaders);
}
