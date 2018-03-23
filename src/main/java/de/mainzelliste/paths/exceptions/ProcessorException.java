package de.mainzelliste.paths.exceptions;

import javax.ws.rs.WebApplicationException;

public class ProcessorException extends WebApplicationException {
    /**
     * Instantiates a new exception.
     */
    public ProcessorException() {
    }

    /**
     * Instantiates a new exception with the given message.
     *
     * @param message the message
     */
    public ProcessorException(String message) {
        super(message);
    }

    /**
     * Instantiates a new exception with the given message and cause.
     *
     * @param message the message
     * @param cause   the underlying cause for this exception
     */
    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new exception with the given cause.
     *
     * @param cause the underlying cause for this exception
     */
    public ProcessorException(Throwable cause) {
        super(cause);
    }
}
