package com.lingona.es4j.domain.api;


/**
 *
 * @author Esfand
 */
public abstract class EventHandlerDelegate<TEvent> {
    
    private final Class<TEvent> eventClass;

    public EventHandlerDelegate(Class<TEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public abstract void handleEvent(Object eventMessage);

    public Class<TEvent> getEventClass() {
        return eventClass;
    }
    //public final IAggregate   aggregate;
    //public final Method       apply;
    //public final EventHandler handler;

    //public EventHandlerDelegate(IAggregate aggregate, Method apply) {
    //    //this.handler   = null;
    //    this.aggregate = aggregate;
    //    this.apply     = apply;
    //}

    //public EventHandlerDelegate(EventHandler handler) {
    //    this.handler   = handler;
    //    this.aggregate = null;
    //    this.apply     = null;
    //}

 
    //public void execute(Object eventMessage) {
    //    this.handler.handleEvent(eventMessage);
    //}
/*
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
*/
}
