/**
 * Class Name: EmailService
 * Package: com.musicApp.backend.features.authentication.utils
 * Date: November 10, 2025
 * Programmer: Jose Bastidas
 *
 * Data Structures:
 * - Uses MimeMessage and MimeMessageHelper (from Jakarta Mail) to structure the email.
 *
 * Algorithms / Design Decisions:
 * - Uses JavaMailSender for email delivery.
 * - The helper ensures proper encoding and allows HTML content in emails.
 * - Designed as a service bean to be injected into other classes (like AuthenticationService)
 *   for separation of concerns and testability.
 * - No complex algorithms; focuses on reliable email delivery.
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

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
    *     Sends an email to the specified recipient with the provided subject and HTML content.
    *     Inputs:
    *       - email: Recipient's email address
    *       - subject: Subject line of the email
    *       - content: Email body (can contain HTML)
    *     Outputs: None
    *     Throws: MessagingException, UnsupportedEncodingException if the email cannot be sent.
     * @param email
     * @param subject
     * @param content
     * @throws MessagingException
     * @throws UnsupportedEncodingException
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