/**
 * Date: November 10, 2025
 * Programmer: Jose Bastidas
 *
 * Data Structures:
 * - Uses MimeMessage and MimeMessageHelper (from Jakarta Mail) to structure the email.
 *
 */

package com.musicApp.backend.features.authentication.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Service class responsible for sending emails through the JavaMailSender.
 * It is used for sending email verification tokens, password reset codes,
 * and any other email notifications to users of the MusicApp backend.
 */
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    /**
     * Constructs the EmailService with a JavaMailSender dependency.
     *
     * @param mailSender the {@link JavaMailSender} instance used to create and send email messages
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
    *     Sends an email to the specified recipient with the provided subject and HTML content.
     * @param email   the recipient's email address (e.g., "user@example.com")
     * @param subject the subject line of the email
     * @param content the body of the email message; may include HTML markup
     * @throws MessagingException             if an error occurs while creating or sending the message
     * @throws UnsupportedEncodingException   if the email encoding format is not supported
     */
    public void sendEmail(String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("no-reply@MoodyMusic.com", "Moody");
        helper.setTo(email);

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

}