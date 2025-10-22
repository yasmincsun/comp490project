package com.musicApp.backend.features.authentication.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("no-reply@MoodyMusic.com", "Moody");
        helper.setTo(email);

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    // // New helper to resend verification emails
    // public void sendVerificationEmail(String email, String verificationToken, long durationInMinutes) {
    //     String subject = "Email Verification";
    //     String body = String.format(
    //         "Only one step to take full advantage of Moody.\n\n" +
    //         "Enter this code to verify your email: %s. The code will expire in %s minutes.",
    //         verificationToken, durationInMinutes
    //     );
    //     try {
    //         sendEmail(email, subject, body);
    //     } catch (Exception e) {
    //         // Log error, don’t throw to avoid breaking flow
    //         System.err.println("Error sending verification email: " + e.getMessage());
    //     }
    // }
}