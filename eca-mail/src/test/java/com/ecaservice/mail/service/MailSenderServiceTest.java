package com.ecaservice.mail.service;

import com.ecaservice.mail.model.Email;
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

import static com.ecaservice.mail.TestHelperUtils.createEmail;
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
class MailSenderServiceTest {

    private static final String MESSAGE = "Message";

    @Mock
    private AesEncryptorService aesEncryptorService;
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private MailSenderService mailSenderService;

    @Test
    void testSuccessSent() throws MessagingException {
        Email email = createEmail(LocalDateTime.now(), EmailStatus.NEW);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(aesEncryptorService.decrypt(email.getMessage())).thenReturn(MESSAGE);
        mailSenderService.sendEmail(email);
        verify(mailSender, atLeastOnce()).send(mimeMessage);
    }
}
