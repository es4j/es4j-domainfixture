package com.lingona.es4j.domain.persistence;

import java.io.Serializable;

public class NameChangedEvent implements IDomainEvent, Serializable {

    private String name; // { get; set; }

    public NameChangedEvent(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
