package com.ecaservice.service.experiment.mail;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.mapping.EmailMapper;
import com.ecaservice.mapping.EmailMapperImpl;
import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Email;
import com.ecaservice.repository.EmailRepository;
import com.ecaservice.service.experiment.AbstractExperimentTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks MailSenderService functionality (see {@link MailSenderService}).
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(EmailMapperImpl.class)
public class MailSenderServiceTest extends AbstractExperimentTest {

    @Mock
    private JavaMailSender mailSender;
    @Autowired
    private EmailMapper emailMapper;
    @Autowired
    private EmailRepository emailRepository;

    @Mock
    private MimeMessage mimeMessage;

    private MailSenderService mailSenderService;

    @Before
    public void setUp() {
        emailRepository.deleteAll();
        mailSenderService = new MailSenderService(mailSender, emailMapper, emailRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMail() throws Exception {
        mailSenderService.sendEmail(null);
    }

    @Test
    public void testSuccessSend() throws Exception {
        Mail mail = TestHelperUtils.createMail();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);
        mailSenderService.sendEmail(mail);
        List<Email> emails = emailRepository.findAll();
        assertNotNull(emails);
        assertEquals(emails.size(), 1);
        Email email = emails.get(0);
        assertNotNull(email.getSaveDate());
        assertEquals(mail.getSender(), email.getSender());
        assertEquals(mail.getReceiver(), email.getReceiver());
        assertEquals(mail.getSubject(), email.getSubject());
        assertEquals(mail.getMessage(), email.getMessage());
    }
}
