package com.lingona.cd4j.persistence;

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
