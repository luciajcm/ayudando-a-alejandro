package org.idea.fithub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicate Resource")
public class DuplicateResourceException extends FitHubException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}