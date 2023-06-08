package com.ecaservice.oauth.service.tfa;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.TfaCodeEntity;
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
import java.time.LocalDateTime;
import java.util.Collections;

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
    private static final String TEST_TOKEN = "testToken";
    private static final String TEST_CODE = "testCode";
    private static final String INVALID_TOKEN = "1";
    private static final String INVALID_CODE = "2";
    private static final String AUTHENTICATION = "auth";

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
        when(serializationHelper.serialize(any())).thenReturn(AUTHENTICATION);
        when(serializationHelper.deserialize(any())).thenReturn(oAuth2Authentication);
    }

    @Override
    public void deleteAll() {
        tfaCodeRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testCreateAuthorizationCode() {
        createAndSaveTfaCode(userEntity);
        var tfaCodeModel = tfaCodeService.createAuthorizationCode(oAuth2Authentication);
        assertThat(tfaCodeModel).isNotNull();
        assertThat(tfaCodeModel.getCode()).hasSize(tfaConfig.getCodeLength());
        assertThat(tfaCodeRepository.count()).isOne();
        var tfaCodeEntity = tfaCodeRepository.findAll().iterator().next();
        assertThat(tfaCodeEntity).isNotNull();
        assertThat(tfaCodeEntity.getUserEntity()).isNotNull();
        assertThat(tfaCodeEntity.getUserEntity().getId()).isEqualTo(userEntity.getId());
        assertThat(tfaCodeEntity.getAuthentication()).isNotNull();
        assertThat(tfaCodeEntity.getCode()).isEqualTo(md5Hex(tfaCodeModel.getCode()));
        assertThat(tfaCodeEntity.getToken()).isEqualTo(md5Hex(tfaCodeModel.getToken()));
    }

    @Test
    void testConsumeAuthorizationCode() {
        var tfaCodeModel = tfaCodeService.createAuthorizationCode(oAuth2Authentication);
        var authentication = tfaCodeService.consumeAuthorizationCode(tfaCodeModel.getToken(), tfaCodeModel.getCode());
        assertThat(authentication).isNotNull();
    }

    @Test
    void testConsumeInvalidAuthorizationCode() {
        assertThrows(InvalidGrantException.class,
                () -> tfaCodeService.consumeAuthorizationCode(INVALID_TOKEN, INVALID_CODE));
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }

    private void createAndSaveTfaCode(UserEntity userEntity) {
        var tfaCodeEntity = new TfaCodeEntity();
        tfaCodeEntity.setUserEntity(userEntity);
        tfaCodeEntity.setToken(md5Hex(TEST_TOKEN));
        tfaCodeEntity.setCode(md5Hex(TEST_CODE));
        tfaCodeEntity.setAuthentication(AUTHENTICATION);
        tfaCodeEntity.setExpireDate(LocalDateTime.now().plusSeconds(tfaConfig.getCodeValiditySeconds()));
        tfaCodeRepository.save(tfaCodeEntity);
    }
}
