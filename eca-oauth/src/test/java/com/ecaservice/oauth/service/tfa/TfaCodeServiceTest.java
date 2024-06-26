package com.ecaservice.oauth.service.tfa;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.TfaCodeEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.TfaCodeRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.security.model.TfaCodeAuthenticationRequest;
import com.ecaservice.oauth.service.AuthenticationJsonSerializer;
import com.ecaservice.user.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link TfaCodeService} class.
 *
 * @author Roman Batygin
 */
@Import({TfaCodeService.class, TfaConfig.class, AuthenticationJsonSerializer.class})
class TfaCodeServiceTest extends AbstractJpaTest {

    private static final String USER = "user";
    private static final String TEST_TOKEN = "testToken";
    private static final String TEST_CODE = "testCode";
    private static final String INVALID_TOKEN = "1";
    private static final String INVALID_CODE = "2";
    private static final String AUTHENTICATION = "auth";
    private static final String CLIENT_ID = "clientId";
    private static final String PASSWORD = "pass";

    @Autowired
    private TfaConfig tfaConfig;
    @Autowired
    private TfaCodeService tfaCodeService;
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private TfaCodeRepository tfaCodeRepository;

    private Authentication authentication;

    private UserEntity userEntity;

    @Override
    public void init() {
        userEntity = createAndSaveUser();
        var authorities = Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_SUPER_ADMIN));
        UserDetails userDetails = new User(USER, PASSWORD, authorities);
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    @Override
    public void deleteAll() {
        tfaCodeRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testCreateAuthorizationCode() {
        createAndSaveTfaCode(userEntity);
        var tfaCodeModel =
                tfaCodeService.createAuthorizationCode(new TfaCodeAuthenticationRequest(CLIENT_ID, authentication));
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
        var tfaCodeModel =
                tfaCodeService.createAuthorizationCode(new TfaCodeAuthenticationRequest(CLIENT_ID, authentication));
        var tfaCodeAuthenticationRequest =
                tfaCodeService.consumeAuthorizationCode(tfaCodeModel.getToken(), tfaCodeModel.getCode());
        assertThat(tfaCodeAuthenticationRequest).isNotNull();
        assertThat(tfaCodeAuthenticationRequest.getClientId()).isEqualTo(CLIENT_ID);
    }

    @Test
    void testConsumeInvalidAuthorizationCode() {
        assertThrows(OAuth2AuthenticationException.class,
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
        tfaCodeEntity.setRegisteredClientId(CLIENT_ID);
        tfaCodeEntity.setToken(md5Hex(TEST_TOKEN));
        tfaCodeEntity.setCode(md5Hex(TEST_CODE));
        tfaCodeEntity.setAuthentication(AUTHENTICATION);
        tfaCodeEntity.setExpireDate(LocalDateTime.now().plusSeconds(tfaConfig.getCodeValiditySeconds()));
        tfaCodeEntity.setCreated(LocalDateTime.now());
        tfaCodeRepository.save(tfaCodeEntity);
    }
}
