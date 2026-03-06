package fr.insee.surveyregistry.exception;

public class InvalidSearchConfigurationException extends RuntimeException {

    public InvalidSearchConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSearchConfigurationException(String message) {
        super(message);
    }
}
