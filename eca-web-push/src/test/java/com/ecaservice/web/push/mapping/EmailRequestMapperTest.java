package com.ecaservice.web.push.mapping;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.web.push.TestHelperUtils;
import com.ecaservice.web.push.config.MailProperties;
import com.ecaservice.web.push.entity.Email;
import com.ecaservice.web.push.entity.EmailStatus;
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
        MailProperties mailProperties = TestHelperUtils.createMailConfig();
        Email email = emailRequestMapper.map(emailRequest, mailProperties);
        Assertions.assertThat(email.getSender()).isEqualTo(mailProperties.getSender());
        Assertions.assertThat(email.getReceiver()).isEqualTo(emailRequest.getReceiver());
        Assertions.assertThat(email.getStatus()).isEqualTo(EmailStatus.NEW);
    }
}
