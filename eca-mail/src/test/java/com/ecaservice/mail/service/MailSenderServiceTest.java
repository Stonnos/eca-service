package com.ecaservice.mail.service;

import com.ecaservice.mail.TestHelperUtils;
import com.ecaservice.mail.model.EmailStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link MailSenderService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
public class MailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private MailSenderService mailSenderService;

    @Test
    void testNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            mailSenderService.sendEmail(null);
        });
    }

    @Test
    void testSuccessSent() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailSenderService.sendEmail(TestHelperUtils.createEmail(LocalDateTime.now(), EmailStatus.NEW));
        verify(mailSender, atLeastOnce()).send(mimeMessage);
    }
}