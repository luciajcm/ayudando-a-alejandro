package org.idea.fithub.exceptions;

public abstract class FitHubException extends RuntimeException {
    protected FitHubException(String message) {
        super(message);
    }

    protected FitHubException(String message, Throwable cause) {
        super(message, cause);
    }
}
