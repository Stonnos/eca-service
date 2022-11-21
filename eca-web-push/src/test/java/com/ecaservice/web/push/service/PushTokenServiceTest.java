package com.ecaservice.web.push.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.web.dto.model.PushTokenDto;
import com.ecaservice.web.push.AbstractJpaTest;
import com.ecaservice.web.push.config.AppProperties;
import com.ecaservice.web.push.config.EncryptConfiguration;
import com.ecaservice.web.push.repository.PushTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.web.push.TestHelperUtils.createPushTokenEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PushTokenService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({EncryptConfiguration.class, AppProperties.class, PushTokenService.class})
class PushTokenServiceTest extends AbstractJpaTest {

    private static final String USER = "user";

    @Inject
    private PushTokenRepository pushTokenRepository;
    @Inject
    private EncryptorBase64AdapterService encryptorBase64AdapterService;
    @Inject
    private AppProperties appProperties;

    @Inject
    private PushTokenService pushTokenService;

    @Override
    public void deleteAll() {
        pushTokenRepository.deleteAll();
    }

    @Test
    void testObtainNewToken() {
        var pushTokenDto = pushTokenService.obtainToken(USER);
        verifyPushToken(pushTokenDto);
    }

    @Test
    void testObtainTokenFromCache() {
        pushTokenService.obtainToken(USER);
        var pushTokenDto = pushTokenService.obtainToken(USER);
        verifyPushToken(pushTokenDto);
    }

    @Test
    void testObtainTokenWithPreviousExpired() {
        createAndSaveExpiredToken();
        var pushTokenDto = pushTokenService.obtainToken(USER);
        verifyPushToken(pushTokenDto);
    }

    private void verifyPushToken(PushTokenDto pushTokenDto) {
        assertThat(pushTokenDto).isNotNull();
        assertThat(pushTokenDto.getTokenId()).isNotNull();
        assertThat(pushTokenRepository.count()).isOne();
        var pushTokenEntity = pushTokenRepository.findByUser(USER);
        assertThat(pushTokenEntity).isNotNull();
        assertThat(pushTokenEntity.getUser()).isEqualTo(USER);
        assertThat(pushTokenEntity.getExpireAt()).isNotNull();
        String expectedTokenId = encryptorBase64AdapterService.encrypt(pushTokenDto.getTokenId());
        assertThat(pushTokenEntity.getTokenId()).isEqualTo(expectedTokenId);
    }

    private void createAndSaveExpiredToken() {
        var pushTokenEntity = createPushTokenEntity(USER, UUID.randomUUID().toString(),
                LocalDateTime.now().minusMinutes(appProperties.getPushTokenValidityMinutes() + 1));
        pushTokenRepository.save(pushTokenEntity);
    }
}
