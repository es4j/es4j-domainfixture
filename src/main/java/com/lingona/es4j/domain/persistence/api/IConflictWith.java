package com.lingona.es4j.domain.persistence.api;

public interface IConflictWith {

    boolean ConflictsWith(Object uncommitted, Object committed);
}
