package com.lingona.es4j.domain.persistence;

import com.lingona.es4j.domain.api.IAggregate;
import com.lingona.es4j.domain.core.AggregateBase;
import java.util.UUID;


public class TestAggregate extends AggregateBase<IDomainEvent> {
		
    private String name; // { get; set; }

    public TestAggregate(UUID id) {
        //this.register /*< TestAggregateCreatedEvent >*/ (this.apply);
        //this.register /*< NameChangedEvent >*/ (this.apply);
        this.setId(id);
    }

    public TestAggregate(UUID id, String name) {
        this(id);
        this.raiseEvent(new TestAggregateCreatedEvent(this.getId(), name));
    }

    //@EventHandler
    private void apply(TestAggregateCreatedEvent event) {
        this.setId(event.getId());
        this.name = event.getName();
    }

    //@EventHandler
    private void apply(NameChangedEvent event) {
        this.name = event.getName();
    }

    public void changeName(String newName) {
        this.raiseEvent(new NameChangedEvent(newName));
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equalsTo(IAggregate other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void throwHandlerNotFound(Object eventMessage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

