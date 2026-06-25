package com.lecturboxd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final long expirationMinutes;
    private final boolean logOnly;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromAddress,
            @Value("${lecturboxd.otp.expiration-minutes}") long expirationMinutes,
            @Value("${lecturboxd.mail.log-only:false}") boolean logOnly
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.expirationMinutes = expirationMinutes;
        this.logOnly = logOnly;
    }

    public void sendVerificationCode(String toEmail, String code) {
        if (logOnly) {
            log.info("DEV OTP for {}: {} (expires in {} min)", toEmail, code, expirationMinutes);
            return;
        }

        log.info("Sending verification email to {}", toEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("LecturBoxd verification code");
        message.setText(
                "Your LecturBoxd verification code is: " + code + "\n\n"
                        + "This code expires in " + expirationMinutes + " minutes."
        );
        try {
            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);
        } catch (MailException ex) {
            log.error("Failed to send verification email to {}", toEmail, ex);
            throw ex;
        }
    }
}
