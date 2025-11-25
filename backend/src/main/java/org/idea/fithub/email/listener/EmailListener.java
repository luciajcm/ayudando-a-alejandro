package org.idea.fithub.email.listener;

import lombok.RequiredArgsConstructor;
import org.idea.fithub.email.service.EmailService;
import org.idea.fithub.event.UserRegisterEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class EmailListener {
    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegister(UserRegisterEvent event) {
        var templateModel = new HashMap<String, Object>();

        templateModel.put("firstName", event.getUser().getFirstName());
        templateModel.put("lastName", event.getUser().getLastName());
        templateModel.put("email", event.getUser().getEmail());
        templateModel.put("phoneNumber", event.getUser().getPhoneNumber());

        emailService.sendEmail(
                event.getUser().getEmail(),
                String.format(
                        "%s your FitHub account is ready",
                        event.getUser().getFirstName()),
                "notification-template",
                templateModel);
    }
}