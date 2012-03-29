package com.lingona.es4j.domain.api;


/**
 *
 * @author Esfand
 */
public abstract class ConflictHandler<TUncommitted, TCommitted> {
    
    private final ConflictHandler handler;
    
    public ConflictHandler(ConflictHandler handler) {
        this.handler = handler;
    }

    public abstract /*delegate*/ boolean conflictsWith(Object uncommitted, Object committed);

}
