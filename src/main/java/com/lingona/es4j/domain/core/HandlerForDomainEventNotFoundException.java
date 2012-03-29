package com.lingona.es4j.domain.core;

//using System.Runtime.Serialization;
public class HandlerForDomainEventNotFoundException extends Exception {

    public HandlerForDomainEventNotFoundException() {
    }

    public HandlerForDomainEventNotFoundException(String message) {
        super(message);
    }

    public HandlerForDomainEventNotFoundException(String message, Exception innerException) {
        super(message, innerException);
    }

    //public HandlerForDomainEventNotFoundException(SerializationInfo info, StreamingContext context) {
    //    super(info, context);
    //}
}
