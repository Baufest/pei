package com.pei.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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

    public void notifyCompliance(Transaction transaction, Alert alert) {
        if (transaction == null || alert == null) {
            throw new IllegalArgumentException("Transaction and Alert cannot be null in notifyCompliance");
        }

    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo("compliance@banco.com.ar"); // destinatario fijo
        helper.setSubject("Alerta: Transacción Internacional Crítica");
        helper.setText(
            alert.description() +
            "ID Transacción: " + transaction.getId() + "\n" +
            "Usuario: " + transaction.getUser().getName() + "\n" +
            "País Origen: " + transaction.getSourceAccount().getCountry() + "\n" +
            "País Destino: " + transaction.getDestinationAccount().getCountry() + "\n" +
            "Monto: $" + transaction.getAmount() + "\n\n"
            ,
            false
        );

        mailSender.send(message);
    } catch (MessagingException e) {
        throw new RuntimeException("Error al enviar correo a Compliance", e);
    }
}
}
