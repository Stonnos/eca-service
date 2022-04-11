package com.ecaservice.core.mail.client.converter;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.notification.dto.EmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.common.web.crypto.factory.EncryptFactory.getAesBytesEncryptor;
import static com.ecaservice.core.mail.client.TestHelperUtils.createEmailRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EmailRequestConverter} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(EcaMailClientProperties.class)
class EmailRequestConverterTest {

    @Inject
    private EcaMailClientProperties ecaMailClientProperties;

    private EmailRequestConverter emailRequestConverter;

    @BeforeEach
    void init() {
        var aesEncryptor = getAesBytesEncryptor(ecaMailClientProperties.getEncrypt().getPassword(),
                ecaMailClientProperties.getEncrypt().getSalt());
        var encryptorAdapter = new EncryptorBase64AdapterService(aesEncryptor);
        emailRequestConverter = new EmailRequestConverter(ecaMailClientProperties, encryptorAdapter);
    }

    @Test
    void testConvertEmailRequest() throws Exception {
        var emailRequest = createEmailRequest();
        String convertedRequest = emailRequestConverter.convert(emailRequest);
        var actual = emailRequestConverter.convert(convertedRequest, EmailRequest.class);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(emailRequest.getTemplateCode());
        assertThat(actual.getReceiver()).isEqualTo(emailRequest.getReceiver());
        assertThat(actual.getPriority()).isEqualTo(emailRequest.getPriority());
        assertThat(actual.getVariables()).hasSameSizeAs(emailRequest.getVariables());
    }
}
