package com.lingona.cd4j.persistence.api;

import com.lingona.cd4j.api.ISaga;
import java.util.UUID;

public interface ISagaRepository {

    <TSaga extends ISaga> TSaga getById(UUID sagaId); // where TSaga : class, ISaga, new();

    void Save(ISaga saga, UUID commitId, HeaderUpdater updateHeaders);
}
