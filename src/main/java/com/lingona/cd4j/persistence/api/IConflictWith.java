package com.lingona.cd4j.persistence.api;

public interface IConflictWith {

    boolean ConflictsWith(Object uncommitted, Object committed);
}
