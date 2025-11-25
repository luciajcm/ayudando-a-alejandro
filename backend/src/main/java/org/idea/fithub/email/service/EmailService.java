package org.idea.fithub.email.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exceptions.EmailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String templateName, Object model) {
        try {
            var context = new Context();
            context.setVariable("data", model);

            var htmlContent = templateEngine.process(templateName, context);

            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("Error sending email: " + e);
        }
    }
    public void sendPasswordResetEmail(String to, String name, String link) {
        try {
            var context = new Context();
            // Usamos un Map para pasar datos a la plantilla
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", name);
            variables.put("link", link);
            context.setVariable("data", variables);

            // NOTA: Asegúrate de crear "password-reset-template.html" en resources/templates
            // O usa el mismo "notification-template" cambiando el texto.
            var htmlContent = templateEngine.process("password-reset-template", context);

            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Recupera tu contraseña - FitHub");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            // Loguear error pero no detener el flujo si no es crítico
            System.err.println("Error enviando correo de recuperación: " + e.getMessage());
        }
    }
}