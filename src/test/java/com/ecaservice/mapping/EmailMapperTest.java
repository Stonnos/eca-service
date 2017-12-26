package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Email;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EmailMapper functionality
 * (see {@link EmailMapper}).
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(EmailMapperImpl.class)
public class EmailMapperTest {

    @Autowired
    private EmailMapper emailMapper;

    @Test
    public void testMapEmail() {
        Mail mail = TestHelperUtils.createMail();
        Email email = emailMapper.map(mail);
        assertThat(mail.getSender()).isEqualTo(email.getSender());
        assertThat(mail.getReceiver()).isEqualTo(email.getReceiver());
        assertThat(mail.getSubject()).isEqualTo(email.getSubject());
        assertThat(mail.getMessage()).isEqualTo(email.getMessage());
    }
}
