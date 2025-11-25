package org.idea.fithub.global;

import jakarta.servlet.http.HttpServletRequest;
import org.idea.fithub.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private String sanitizeMessage(String message) {
        if (message == null) return "An error occurred";
        return HtmlUtils.htmlEscape(message);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        var status = HttpStatus.NOT_FOUND;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Not Found",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex, HttpServletRequest request) {
        var status = HttpStatus.CONFLICT;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Conflict",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request) {
        var status = HttpStatus.CONFLICT;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Conflict",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(
            UserAlreadyExistException ex, HttpServletRequest request) {
        var status = HttpStatus.CONFLICT;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Conflict",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Bad Request",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        assert ex.getRequiredType() != null;
        var message = String.format("Parameter '%s' should be of type '%s'",
                sanitizeMessage(ex.getName()),
                sanitizeMessage(ex.getRequiredType().getSimpleName()));
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Bad Request",
                message,
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(
            InvalidOperationException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Invalid Operation",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        var status = HttpStatus.UNAUTHORIZED;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Unauthorized",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            Exception ex, HttpServletRequest request) {
        var status = HttpStatus.UNAUTHORIZED;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Unauthorized",
                "Invalid credentials or authentication failed",
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException ex, HttpServletRequest request) {
        var status = HttpStatus.FORBIDDEN;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Forbidden",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        var status = HttpStatus.FORBIDDEN;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Forbidden",
                "You don't have permission to access this resource",
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var message = "Required parameter '" + sanitizeMessage(ex.getParameterName()) + "' is missing";
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Bad Request",
                message,
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Bad Request",
                "Invalid request body or malformed JSON",
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Bad Request",
                sanitizeMessage(errors),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex, HttpServletRequest request) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ErrorResponse> handleEmailSendException(
            EmailSendException ex, HttpServletRequest request) {
        var status = HttpStatus.SERVICE_UNAVAILABLE;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Service Unavailable",
                sanitizeMessage(ex.getMessage()),
                sanitizeMessage(request.getRequestURI())
        );
        return ResponseEntity.status(status).body(error);
    }
}