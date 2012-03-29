package com.lingona.es4j.domain.persistence.api;

import com.lingona.es4j.api.domain.IAggregate;
import java.util.Map;
import java.util.UUID;


public class RepositoryExtensions {
    
    public static void Save(/*this*/ IRepository repository, IAggregate aggregate, UUID commitId) {
        
        HeaderUpdater headerUpdater = new HeaderUpdater() {
            @Override
            public void updateHeader(Map<String, Object> map) {
                // no op
            }
        };
        repository.save(aggregate, commitId, headerUpdater);
    }
}
