package com.pei.service.alertnotificator;

import com.pei.domain.User;
import com.pei.dto.TransactionDTO;
import com.pei.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Componente / Servicio para enviar notificaciones de alerta crítica por email
// Implementa la estrategia de notificación de alertas críticas a través de correo electrónico
// Utiliza JavaMailSender para enviar correos electrónicos a los usuarios registrados
// Maneja excepciones específicas relacionadas con el envío de correos electrónicos
@Component
@Primary
public class AlertNotificatorEmail implements AlertNotificatorStrategy {
    private final Logger logger = LoggerFactory.getLogger(AlertNotificatorEmail.class);

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String from;

    public AlertNotificatorEmail(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    // Metodo para enviar una alerta crítica a un usuario específico
    // Busca al usuario por ID, verifica si tiene un email registrado y envía la notificación
    // Si el usuario no existe o no tiene email, se registra un mensaje de error
    // Si ocurre un error al enviar la notificación, se lanza una excepción personalizada
    @Override
    public void sendCriticalAlert(Long userId, TransactionDTO transactionDTO) throws AlertNotificatorException {
        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                logger.error("Usuario con ID {} no encontrado", userId);
                return;
            }

            User user = userOptional.get();

            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                logger.warn("El usuario con ID {} no tiene un email registrado", userId);
                return;
            }

            executeEmailNotificator(user, transactionDTO);
            logger.info("Notificación enviada a usuario con ID: {}", userId);
        } catch (Exception e) {
            logger.error("No se pudo enviar la notificación al usuario con ID {}: {}", userId, e.getMessage());
            throw new AlertNotificatorException(String.format(
                "No se pudo enviar la notificación por el service de Email al usuario con ID %d: %s",
                userId,
                e.getMessage()
            ));
        }
    }

    // Se encarga de crear y enviar el correo electrónico de alerta crítica
    // Utiliza JavaMailSender para crear un MimeMessage y MimeMessageHelper
    public void executeEmailNotificator(User user, TransactionDTO transactionDTO) throws AlertNotificatorException {
        try {
            final String emailBody = generarCuerpoEmail(transactionDTO);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject("Alerta de Transacción Crítica");
            helper.setText(emailBody, false);

            mailSender.send(message);
            logger.info("Correo de alerta crítica enviado a usuario con ID: {}", user.getId());
        } catch (MessagingException e) {
            logger.error("Error al enviar correo de alerta crítica: {}", e.getMessage());

            throw new RuntimeException("Error al enviar correo de alerta crítica", e);
        }
    }

    /**
     * Genera el cuerpo del email de alerta crítica para una transacción.
     *
     * @param transactionDTO DTO de la transacción crítica
     * @return cuerpo del email como String
     */
    private String generarCuerpoEmail(final TransactionDTO transactionDTO) {
        return "Estimado usuario,\n\n" +
            "La transacción con ID " + transactionDTO.id() +
            " y monto " + transactionDTO.amount() +
            " ha sido marcada como de ALTA criticidad.\n\n" +
            "Por favor, revise su cuenta lo antes posible.\n\n" +
            "Atentamente,\n" +
            "Equipo de Seguridad Bancaria";
    }
}
