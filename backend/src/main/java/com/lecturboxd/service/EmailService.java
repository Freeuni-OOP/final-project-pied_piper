package com.lecturboxd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EN: Sends verification emails (or logs OTP in development when mail is log-only).
 * KA: აგზავნის ვერიფიკაციის ელფოსტებს (ან ლოგავს OTP-ს დეველოპმენტში, როცა მეილი მხოლოდ ლოგია).
 */
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

    /**
     * EN: Delivers a verification OTP to the given email address.
     * KA: აგზავნის ვერიფიკაციის OTP-ს მოცემულ ელფოსტის მისამართზე.
     */
    public void sendVerificationCode(String toEmail, String code) {
        // EN: Dev mode — log OTP instead of sending mail | KA: დევ რეჟიმი — OTP-ის ლოგირება მეილის ნაცვლად
        if (logOnly) {
            log.info("DEV OTP for {}: {} (expires in {} min)", toEmail, code, expirationMinutes);
            return;
        }

        // EN: Build and send verification email | KA: ვერიფიკაციის ელფოსტის აგება და გაგზავნა
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
            // EN: Side effect — SMTP send | KA: გვერდითი ეფექტი — SMTP გაგზავნა
            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);
        } catch (MailException ex) {
            log.error("Failed to send verification email to {}", toEmail, ex);
            throw ex;
        }
    }
}
