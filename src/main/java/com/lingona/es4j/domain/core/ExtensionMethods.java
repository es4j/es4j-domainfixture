package com.lingona.es4j.domain.core;

import com.lingona.es4j.api.domain.IAggregate;
import java.util.Locale;
//using System.Globalization;


public class ExtensionMethods { // internal static
	
    public static String formatWith(/*this*/ String format, Object... args) {
	//return string.Format(CultureInfo.InvariantCulture, format ?? string.Empty, args);
        return String.format(Locale.getDefault(), "".equals(format)?"":format, args);
    }

    public static void throwHandlerNotFound(/*this*/ IAggregate aggregate, Object eventMessage) {
	String exceptionMessage = String.format("Aggregate of type '%s' raised an event of type '%s' but not handler could be found to handle the message.",
				             new Object[] {aggregate.getClass().getName(), eventMessage.getClass().getName()});
        try {
            throw new HandlerForDomainEventNotFoundException(exceptionMessage);
        } catch (HandlerForDomainEventNotFoundException ex) {
            //Logger.getLogger(ExtensionMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}