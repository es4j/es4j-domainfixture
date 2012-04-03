package com.lingona.es4j.domain.api;


public interface IDetectConflicts {

    public <TUncommitted, TCommitted> void register(ConflictDelegate handler); //where TUncommitted : class where TCommitted : class

    boolean conflictsWith(Iterable<Object> uncommittedEvents, Iterable<Object> committedEvents);
}

// public delegate boolean ConflictDelegate(Object uncommitted, Object committed);
