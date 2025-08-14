package com.pei.service;

import com.pei.domain.User;
import com.pei.domain.Transaction;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCriticalAlertEmail(final User user, final Transaction transaction) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject("Alerta de Transacción Crítica");
            helper.setText(
                "Estimado " + user.getName() + ",\n\n" +
                    "La transacción con ID " + transaction.getId() +
                    " y monto " + transaction.getAmount() +
                    " ha sido marcada como de ALTA criticidad.\n\n" +
                    "Por favor, revise su cuenta lo antes posible.\n\n" +
                    "Atentamente,\n" +
                    "Equipo de Seguridad Bancaria"
                    ,
                false
            );

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de alerta crítica", e);
        }
    }
}
