package com.lingona.cd4j.persistence.api;

import com.lingona.cd4j.api.IAggregate;
import java.util.UUID;


public class RepositoryExtensions {
    
    public static void Save(/*this*/ IRepository repository, IAggregate aggregate, UUID commitId) {
             repository.save(aggregate, commitId, a => {});
    }
}
