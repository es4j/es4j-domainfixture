package com.lingona.es4j.domain.api;


public abstract class ConflictDelegate/*<TUncommitted, TCommitted>*/ {

    private final Class<Object> classUncommitted;
    private final Class<Object> classCommitted;

    public ConflictDelegate(Class<Object> classUncommitted, Class<Object> classCommitted) {
        this.classUncommitted = classUncommitted;
        this.classCommitted   = classCommitted;
    }

    public abstract boolean conflictsWith(Object uncommitted, Object committed); // delegate

    public Class<Object> getClassCommitted() {
        return classCommitted;
    }

    public Class<Object> getClassUncommitted() {
        return classUncommitted;
    }
}
