package com.ecaservice.oauth.service.tfa;

import com.ecaservice.oauth.config.TfaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TfaCodeService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({TfaCodeService.class, TfaConfig.class})
class TfaCodeServiceTest {

    private static final String USER = "user";

    @Inject
    private TfaConfig tfaConfig;
    @Inject
    private TfaCodeService tfaCodeService;

    @Mock
    private OAuth2Authentication oAuth2Authentication;

    @BeforeEach
    void init() {
        when(oAuth2Authentication.getName()).thenReturn(USER);
    }

    @Test
    void testCreateAuthorizationCode() {
        String code = tfaCodeService.createAuthorizationCode(oAuth2Authentication);
        assertThat(code).hasSize(tfaConfig.getCodeLength());
    }

    @Test
    void testConsumeAuthorizationCode() {
        String code = tfaCodeService.createAuthorizationCode(oAuth2Authentication);
        OAuth2Authentication authentication = tfaCodeService.consumeAuthorizationCode(code);
        assertThat(authentication).isNotNull();
    }

    @Test
    void testConsumeInvalidAuthorizationCode() {
        String code = UUID.randomUUID().toString();
        assertThrows(InvalidGrantException.class, () -> tfaCodeService.consumeAuthorizationCode(code));
    }
}
