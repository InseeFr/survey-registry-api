package fr.insee.surveyregistry.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problemDetail.setTitle("Resource not found");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleResourceAlreadyExists(
            ResourceAlreadyExistsException exception,
            HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problemDetail.setTitle("Resource already exists");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ProblemDetail handleInvalidRequest(
            InvalidRequestException exception,
            HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Invalid request");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }
}