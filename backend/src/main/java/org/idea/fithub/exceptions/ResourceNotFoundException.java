package org.idea.fithub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not Found")
public class ResourceNotFoundException extends FitHubException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}