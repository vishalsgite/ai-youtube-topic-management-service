package com.vishal.aiyoutube.topic_management_service.exceptions;

/**
 * Exception thrown when the consumer fails to process or persist
 * an AnalysisCompletedEvent received from Kafka.
 */
public class AnalysisProcessingException extends RuntimeException {
    public AnalysisProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
