package com.vishal.aiyoutube.topic_management_service.exceptions;

/**
 * Exception thrown when the consumer fails to update the lifecycle status
 * of a research topic in the database.
 */
public class StatusSyncException extends RuntimeException {
    public StatusSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
