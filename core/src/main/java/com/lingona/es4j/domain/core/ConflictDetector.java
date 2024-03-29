package com.lingona.es4j.domain.core;

import com.lingona.es4j.domain.api.ConflictDelegate;
import com.lingona.es4j.domain.api.IDetectConflicts;
import java.util.HashMap;
import java.util.Map;


// The conflict detector is used to determine if the events to be committed represent
// a true business conflict as compared to events that have already been committed, thus
// allowing reconciliation of optimistic concurrency problems.
// The implementation contains some internal lambda "magic" which allows casting between
// TCommitted, TUncommitted, and System.Object and in a completely type-safe way.
public class ConflictDetector implements IDetectConflicts {
 
    private final Map<Class, Map<Class, ConflictDelegate>> actions = new HashMap<Class, Map<Class, ConflictDelegate>>();


    @Override
    public <TUncommitted, TCommitted> void register(ConflictDelegate handler) { //where TUncommitted : class where TCommitted : class
        if(true) throw new UnsupportedOperationException("Not yet implemented");
        
	//if (!this.actions.TryGetValue(typeof(TUncommitted), out inner))
	//    this.actions[typeof(TUncommitted)] = inner = new Dictionary<Type, ConflictDelegate>();        
        Map<Class, ConflictDelegate> inner;
        Class classUncommitted = handler.getClassUncommitted();
        if(this.actions.containsKey(classUncommitted)) {
            inner = this.actions.get(classUncommitted);
        }
        else {
            inner = new HashMap<Class, ConflictDelegate>();
        }

	//inner[typeof(TCommitted)] = (uncommitted, committed) =>
	//			handler(uncommitted as TUncommitted, committed as TCommitted);
        //ConflictHandler<TUncommitted, TCommitted> delegate = new ConflictHandler<TUncommitted, TCommitted>(handler) {
            //@Override
            //public boolean callback(Object uncommitted, Object committed) {
            //    return handler.callback(uncommitted, committed);
            //}
        //};
        //Class classCommitted = TCommitted.getClass();
        //inner.put(classCommitted, handler.callback(delegate, delegate));
    }

           
    @Override
    public boolean conflictsWith(Iterable<Object> uncommittedEvents, Iterable<Object> committedEvents) {
                    
        for(Object uncommitted : uncommittedEvents) {       
            for(Object committed : committedEvents) {
                if(this.conflicts(uncommitted, committed)) {
                    return true;
                }
            }
        }
        return false;             
    }

    
    private boolean conflicts(Object uncommitted, Object committed) {

        if(!this.actions.containsKey(uncommitted.getClass())) {
            // no registration, only conflict if the events are the same time
            return uncommitted.getClass() == committed.getClass();
        }
        Map<Class, ConflictDelegate> registration = this.actions.get(uncommitted.getClass());
                                                                 
        if(!registration.containsKey(committed.getClass())) {
            return true;
        }
        ConflictDelegate handler = registration.get(committed.getClass());

        return handler.conflictsWith(uncommitted, committed);
    }

}
