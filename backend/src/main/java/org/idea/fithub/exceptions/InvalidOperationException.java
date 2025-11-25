package org.idea.fithub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid Operation")
public class InvalidOperationException extends FitHubException {
    public InvalidOperationException(String message) {
        super(message);
    }
}