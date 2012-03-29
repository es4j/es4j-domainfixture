package com.lingona.es4j.domain.persistence.api;

/// Represents a general failure of the persistence infrastructure.
public class PersistenceException extends Exception {

    /// Initializes a new instance of the PersistenceException class.
    public PersistenceException() {
    }

    /// Initializes a new instance of the PersistenceException class.
    /// <param name="message">The message that describes the error.</param>
    public PersistenceException(String message) {
        super(message);
    }

    /// Initializes a new instance of the PersistenceException class.
    /// <param name="message">The message that describes the error.</param>
    /// <param name="innerException">The message that is the cause of the current exception.</param>
    public PersistenceException(String message, Exception innerException) {
        super(message, innerException);
    }

    /// Initializes a new instance of the PersistenceException class.
    /// <param name="info">The SerializationInfo that holds the serialized object data of the exception being thrown.</param>
    /// <param name="context">The StreamingContext that contains contextual information about the source or destination.</param>
    //protected PersistenceException(SerializationInfo info, StreamingContext context) {
    //    super(info, context);
    //}
}