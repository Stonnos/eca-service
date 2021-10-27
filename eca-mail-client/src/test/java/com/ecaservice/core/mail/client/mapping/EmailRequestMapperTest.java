package com.ecaservice.core.mail.client.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.core.mail.client.TestHelperUtils.createEmailRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EmailRequestMapper} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(EmailRequestMapperImpl.class)
class EmailRequestMapperTest {

    @Inject
    private EmailRequestMapper emailRequestMapper;

    @Test
    void testMapEmailRequest() {
        var emailRequest = createEmailRequest();
        var emailRequestEntity = emailRequestMapper.map(emailRequest);
        assertThat(emailRequestEntity).isNotNull();
        assertThat(emailRequestEntity.getPriority()).isEqualTo(emailRequest.getPriority());
        assertThat(emailRequestEntity.getReceiver()).isEqualTo(emailRequest.getReceiver());
        assertThat(emailRequestEntity.getTemplateCode()).isEqualTo(emailRequest.getTemplateCode());
    }
}
