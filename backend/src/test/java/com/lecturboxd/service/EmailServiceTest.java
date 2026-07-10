package com.lecturboxd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void logOnlySkipsSending() {
        EmailService emailService = new EmailService(mailSender, "from@test.com", 10, true);

        emailService.sendVerificationCode("user@freeuni.edu.ge", "123456");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendBuildsMessageAndSends() {
        EmailService emailService = new EmailService(mailSender, "from@test.com", 15, false);

        emailService.sendVerificationCode("user@freeuni.edu.ge", "654321");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertEquals("from@test.com", message.getFrom());
        assertEquals("user@freeuni.edu.ge", message.getTo()[0]);
        assertEquals("LecturBoxd verification code", message.getSubject());
        assertEquals(true, message.getText().contains("654321"));
        assertEquals(true, message.getText().contains("15 minutes"));
    }

    @Test
    void sendRethrowsMailException() {
        EmailService emailService = new EmailService(mailSender, "from@test.com", 10, false);
        doThrow(new MailSendException("smtp down")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(MailException.class,
                () -> emailService.sendVerificationCode("user@freeuni.edu.ge", "111111"));
    }
}
