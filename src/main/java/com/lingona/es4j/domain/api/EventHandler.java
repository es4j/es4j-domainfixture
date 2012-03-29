package com.lingona.es4j.domain.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 *
 * @author Esfand
 */
public abstract class EventHandler<T> {
    public final IAggregate   aggregate;
    public final Method       apply;
    public final EventHandler handler;
    
    public EventHandler(IAggregate aggregate, Method apply) {
        this.handler   = null;
        this.aggregate = aggregate;
        this.apply     = apply;
    }
    
    public EventHandler(EventHandler handler) {
        this.handler   = handler;
        this.aggregate = null;
        this.apply     = null;
    }
    
    public abstract void handleEvent(Object eventMessage);
    
    
    public void execute(Object eventMessage) {
        this.handler.handleEvent(eventMessage);
    }
    
    public void executeApplyOnAggregate(Object eventMessage) {
        try {
            apply.invoke(aggregate, eventMessage);
        } catch (IllegalAccessException ex) {
            //Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            //Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            //Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
