package com.ecaservice.mail.mapping;

import com.ecaservice.mail.TestHelperUtils;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.EmailStatus;
import com.ecaservice.notification.dto.EmailRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for checking {@link EmailRequestMapper} functionality.
 */
@ExtendWith(SpringExtension.class)
@Import(EmailRequestMapperImpl.class)
class EmailRequestMapperTest {

    @Autowired
    private EmailRequestMapper emailRequestMapper;

    @Test
    void testMapEmailRequest() {
        EmailRequest emailRequest = TestHelperUtils.createEmailRequest();
        MailConfig mailConfig = TestHelperUtils.createMailConfig();
        Email email = emailRequestMapper.map(emailRequest, mailConfig);
        Assertions.assertThat(email.getSender()).isEqualTo(mailConfig.getSender());
        Assertions.assertThat(email.getReceiver()).isEqualTo(emailRequest.getReceiver());
        Assertions.assertThat(email.getStatus()).isEqualTo(EmailStatus.NEW);
    }
}
