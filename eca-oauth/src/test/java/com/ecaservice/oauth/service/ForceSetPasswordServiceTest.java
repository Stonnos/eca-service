package com.ecaservice.oauth.service;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.PasswordEncoderTestConfiguration;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.ForceSetPasswordRequest;
import com.ecaservice.oauth.entity.ForceSetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.PasswordsMatchedException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ForceSetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link ForceSetPasswordService} functionality.
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, PasswordRuleHandler.class, PasswordValidationService.class, ObjectMapper.class,
        PasswordEncoderTestConfiguration.class, ForceSetPasswordService.class})
class ForceSetPasswordServiceTest extends AbstractJpaTest {

    private static final String PASSWORD = "@pa66word!";
    private static final String NEW_PASSWORD = "#123dCgrh56$f";
    private static final String TOKEN = "token";
    private static final String CONFIRMATION_CODE = "112456";

    @MockBean
    private Oauth2RevokeTokenService oauth2RevokeTokenService;

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private PasswordValidationService passwordValidationService;
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private ForceSetPasswordRequestRepository forceSetPasswordRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ForceSetPasswordService forceSetPasswordService;

    private UserEntity userEntity;

    @Override
    public void init() {
        userEntity = createAndSaveUser();
    }

    @Override
    public void deleteAll() {
        forceSetPasswordRequestRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testCreateNewForceSetPasswordRequest() {
        TokenModel tokenModel = forceSetPasswordService.createForceSetPasswordRequest(userEntity);
        assertThat(tokenModel).isNotNull();
        assertThat(tokenModel.getTokenId()).isNotNull();
        assertThat(tokenModel.getLogin()).isNotNull();
        assertThat(tokenModel.getEmail()).isNotNull();
        assertThat(tokenModel.getToken()).isNotNull();
        assertThat(tokenModel.getConfirmationCode()).isNotNull();
        var requestEntity =
                forceSetPasswordRequestRepository.findById(tokenModel.getTokenId()).orElse(null);
        assertThat(requestEntity).isNotNull();
        assertThat(requestEntity.getExpireDate()).isNotNull();
        assertThat(requestEntity.getToken()).isNotNull();
        assertThat(requestEntity.getConfirmationCode()).isNotNull();
        assertThat(requestEntity.getUserEntity()).isNotNull();
        assertThat(requestEntity.getPasswordDate()).isNull();
    }

    @Test
    void testCreateForceSetPasswordRequestForLockedUser() {
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        assertThrows(UserLockedException.class,
                () -> forceSetPasswordService.createForceSetPasswordRequest(userEntity));
    }

    @Test
    void testForceSetPasswordForNotExistingToken() {
        var forceSetPasswordRequest =
                new ForceSetPasswordRequest(UUID.randomUUID().toString(), CONFIRMATION_CODE, PASSWORD);
        assertThrows(InvalidTokenException.class,
                () -> forceSetPasswordService.forceSetPassword(forceSetPasswordRequest));
    }

    @Test
    void testForceSetPasswordForAlreadySetRequest() {
        var requestEntity = createAndSaveRequestEntity(LocalDateTime.now().plusMinutes(5L),
                LocalDateTime.now().plusMinutes(2L));
        ForceSetPasswordRequest forceSetPasswordRequest =
                new ForceSetPasswordRequest(requestEntity.getToken(), CONFIRMATION_CODE, PASSWORD);
        assertThrows(InvalidTokenException.class,
                () -> forceSetPasswordService.forceSetPassword(forceSetPasswordRequest));
    }

    @Test
    void testResetPasswordForExpiredToken() {
        var requestEntity = createAndSaveRequestEntity(LocalDateTime.now().minusMinutes(1L), null);
        ForceSetPasswordRequest forceSetPasswordRequest =
                new ForceSetPasswordRequest(requestEntity.getToken(), CONFIRMATION_CODE, PASSWORD);
        assertThrows(InvalidTokenException.class,
                () -> forceSetPasswordService.forceSetPassword(forceSetPasswordRequest));
    }

    @Test
    void testForceSetPasswordSuccess() {
        var requestEntity = forceSetPasswordService.createForceSetPasswordRequest(userEntity);
        ForceSetPasswordRequest forceSetPasswordRequest =
                new ForceSetPasswordRequest(requestEntity.getToken(), requestEntity.getConfirmationCode(),
                        NEW_PASSWORD);
        forceSetPasswordService.forceSetPassword(forceSetPasswordRequest);
        var actual = forceSetPasswordRequestRepository.findById(requestEntity.getTokenId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getPasswordDate()).isNotNull();
        assertThat(actual.getUserEntity().getPasswordChangeDate()).isNotNull();
        assertThat(actual.getUserEntity().getPassword()).isNotNull();
        assertThat(actual.getUserEntity().getPasswordChangeDate()).isNotNull();
        assertThat(actual.getUserEntity().isForceChangePassword()).isFalse();
        verify(oauth2RevokeTokenService, atLeastOnce()).revokeTokens(any(UserEntity.class));
    }

    @Test
    void testForceSetPasswordForLockedUser() {
        var requestEntity = forceSetPasswordService.createForceSetPasswordRequest(userEntity);
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        ForceSetPasswordRequest forceSetPasswordRequest =
                new ForceSetPasswordRequest(requestEntity.getToken(), CONFIRMATION_CODE, NEW_PASSWORD);
        assertThrows(UserLockedException.class,
                () -> forceSetPasswordService.forceSetPassword(forceSetPasswordRequest));
    }

    @Test
    void testForceSetPasswordWithSamePasswordShouldThrowPasswordMatchedException() {
        var requestEntity = forceSetPasswordService.createForceSetPasswordRequest(userEntity);
        ForceSetPasswordRequest forceSetPasswordRequest =
                new ForceSetPasswordRequest(requestEntity.getToken(), CONFIRMATION_CODE, PASSWORD);
        assertThrows(PasswordsMatchedException.class,
                () -> forceSetPasswordService.forceSetPassword(forceSetPasswordRequest));
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setPassword(passwordEncoder.encode(PASSWORD));
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }

    private ForceSetPasswordRequestEntity createAndSaveRequestEntity(LocalDateTime expireDate,
                                                                     LocalDateTime passwordDate) {
        ForceSetPasswordRequestEntity resetPasswordRequestEntity = new ForceSetPasswordRequestEntity();
        resetPasswordRequestEntity.setToken(md5Hex(TOKEN));
        resetPasswordRequestEntity.setExpireDate(expireDate);
        resetPasswordRequestEntity.setConfirmationCode(md5Hex(CONFIRMATION_CODE));
        resetPasswordRequestEntity.setPasswordDate(passwordDate);
        resetPasswordRequestEntity.setUserEntity(userEntity);
        resetPasswordRequestEntity.setCreated(LocalDateTime.now());
        return forceSetPasswordRequestRepository.save(resetPasswordRequestEntity);
    }
}
