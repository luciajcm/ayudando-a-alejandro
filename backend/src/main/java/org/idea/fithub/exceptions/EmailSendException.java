package org.idea.fithub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Error sending email")
public class EmailSendException extends FitHubException {
    public EmailSendException(String message) {
        super(message);
    }
}