package com.lingona.cd4j.persistence.api;

/// Represents a command that could not be executed because it conflicted with the command of another user or actor.
public class ConflictingCommandException extends Exception {

    /// Initializes a new instance of the ConflictingCommandException class.
    public ConflictingCommandException() {
    }

    /// Initializes a new instance of the ConflictingCommandException class.
    /// <param name="message">The message that describes the error.</param>
    public ConflictingCommandException(String message) {
        super(message);
    }

    /// Initializes a new instance of the ConflictingCommandException class.
    /// <param name="message">The message that describes the error.</param>
    /// <param name="innerException">The message that is the cause of the current exception.</param>
    public ConflictingCommandException(String message, Exception innerException) {
        super(message, innerException);
    }

    /// Initializes a new instance of the ConflictingCommandException class.
    /// <param name="info">The SerializationInfo that holds the serialized object data of the exception being thrown.</param>
    /// <param name="context">The StreamingContext that contains contextual information about the source or destination.</param>
    //protected ConflictingCommandException(SerializationInfo info, StreamingContext context) {
    //    super(info, context);
    //}
}
