package com.lecturboxd.config;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * EN: Builds a JavaMailSender from Spring Boot mail properties for OTP email delivery.
 * KA: აგებს JavaMailSender-ს Spring Boot-ის mail თვისებებიდან OTP ელფოსტის გაგზავნისთვის.
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {

    private final MailProperties mailProperties;

    /**
     * EN: Injects Spring Boot MailProperties used to configure the mail sender.
     * KA: ინჯექციას უკეთებს Spring Boot-ის MailProperties-ს, რომლითაც კონფიგურდება მეილის გამგზავნი.
     */
    public MailConfig(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    /**
     * EN: Creates and configures JavaMailSenderImpl with host, credentials, and JavaMail properties.
     * KA: ქმნის და აკონფიგურირებს JavaMailSenderImpl-ს ჰოსტით, რწმუნებებით და JavaMail თვისებებით.
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        if (mailProperties.getProtocol() != null) {
            mailSender.setProtocol(mailProperties.getProtocol());
        }

        Properties javaMailProperties = new Properties();
        javaMailProperties.putAll(mailProperties.getProperties());
        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }
}
