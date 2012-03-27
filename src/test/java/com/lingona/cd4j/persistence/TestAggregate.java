package com.lingona.cd4j.persistence;


public class TestAggregate extends AggregateBase<IDomainEvent> {
		
    private String name; // { get; set; }

    public TestAggregate(UUID id) {
        this.Register < TestAggregateCreatedEvent > (this.Apply);
        this.Register < NameChangedEvent > (this.Apply);
        this.Id = id;
    }

    public TestAggregate(UUID id, String name) {
        this(id);
        this.raiseEvent(new TestAggregateCreatedEvent(this.getId(), name);
    }

    private void apply(TestAggregateCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
    }

    public void changeName(String newName) {
        this.raiseEvent(new NameChangedEvent(newName));
    }

    private void apply(NameChangedEvent event) {
        this.name = event.getName();
    }

    public String getName() {
        return name;
    }
    
}

