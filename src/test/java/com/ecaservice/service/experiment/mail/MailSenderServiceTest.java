package com.ecaservice.service.experiment.mail;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.model.entity.Email;
import com.ecaservice.repository.EmailRepository;
import com.ecaservice.service.experiment.AbstractExperimentTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks MailSenderService functionality {@see MailSenderService}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
public class MailSenderServiceTest extends AbstractExperimentTest {

    @Mock
    private JavaMailSender mailSender;
    @Inject
    private EmailRepository emailRepository;

    @Mock
    private MimeMessage mimeMessage;

    private MailSenderService mailSenderService;

    @Before
    public void setUp() {
        emailRepository.deleteAll();
        mailSenderService = new MailSenderService(mailSender, emailRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMail() {
        mailSenderService.sendEmail(null);
    }

    @Test
    public void testSuccessSend() {
        Email mail = TestHelperUtils.createEmail();
        emailRepository.save(mail);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);
        mailSenderService.sendEmail(mail);
        List<Email> emails = emailRepository.findAll();
        AssertionUtils.assertSingletonList(emails);
        Email email = emails.get(0);
        assertThat(email.getSentDate()).isNotNull();
        assertThat(email.isSent()).isTrue();
    }

    @Test(expected = EcaServiceException.class)
    public void testErrorSend() {
        Email mail = TestHelperUtils.createEmail();
        emailRepository.save(mail);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException()).when(mailSender).send(mimeMessage);
        mailSenderService.sendEmail(mail);
    }
}
