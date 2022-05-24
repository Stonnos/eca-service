package com.ecaservice.oauth.service.tfa;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.TfaCodeRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.SerializationHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.inject.Inject;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TfaCodeService} class.
 *
 * @author Roman Batygin
 */
@Import({TfaCodeService.class, TfaConfig.class})
class TfaCodeServiceTest extends AbstractJpaTest {

    private static final String USER = "user";

    @MockBean
    private SerializationHelper serializationHelper;

    @Inject
    private TfaConfig tfaConfig;
    @Inject
    private TfaCodeService tfaCodeService;
    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private TfaCodeRepository tfaCodeRepository;

    @Mock
    private OAuth2Authentication oAuth2Authentication;

    private UserEntity userEntity;

    @Override
    public void init() {
        userEntity = createAndSaveUser();
        when(oAuth2Authentication.getName()).thenReturn(USER);
        when(serializationHelper.serialize(any())).thenReturn(new byte[0]);
        when(serializationHelper.deserialize(any())).thenReturn(oAuth2Authentication);
    }

    @Override
    public void deleteAll() {
        tfaCodeRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testCreateAuthorizationCode() {
        String code = tfaCodeService.createAuthorizationCode(oAuth2Authentication);
        assertThat(code).hasSize(tfaConfig.getCodeLength());
        assertThat(tfaCodeRepository.count()).isOne();
        var tfaCodeEntity = tfaCodeRepository.findAll().iterator().next();
        assertThat(tfaCodeEntity).isNotNull();
        assertThat(tfaCodeEntity.getUserEntity()).isNotNull();
        assertThat(tfaCodeEntity.getUserEntity().getId()).isEqualTo(userEntity.getId());
        assertThat(tfaCodeEntity.getAuthentication()).isNotNull();
        assertThat(tfaCodeEntity.getToken()).isEqualTo(md5Hex(code));
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

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }
}
