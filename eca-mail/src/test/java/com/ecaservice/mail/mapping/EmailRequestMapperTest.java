package com.ecaservice.mail.mapping;

import com.ecaservice.mail.TestHelperUtils;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.EmailStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * Unit tests for checking {@link EmailRequestMapper} functionality.
 */
@ExtendWith(SpringExtension.class)
@Import(EmailRequestMapperImpl.class)
class EmailRequestMapperTest {

    @Inject
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

    @Test
    void testMapEmail() {
        Email email = TestHelperUtils.createEmail(LocalDateTime.now(), EmailStatus.NEW);
        EmailResponse emailResponse = emailRequestMapper.map(email);
        Assertions.assertThat(emailResponse).isNotNull();
        Assertions.assertThat(emailResponse.getRequestId()).isEqualTo(email.getUuid());
    }
}
