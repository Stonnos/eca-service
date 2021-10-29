package com.ecaservice.oauth.service;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.CreateResetPasswordRequest;
import com.ecaservice.oauth.dto.ResetPasswordRequest;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.PasswordsMatchedException;
import com.ecaservice.oauth.exception.ResetPasswordRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
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
 * Unit tests for checking {@link ResetPasswordService} functionality.
 *
 * @author Roman Batygin
 */
@Import(AppProperties.class)
class ResetPasswordServiceTest extends AbstractJpaTest {

    private static final String PASSWORD = "@pa66word!";
    private static final String NEW_PASSWORD = "@p#a66word!";
    private static final String TOKEN = "token";

    @Inject
    private AppProperties appProperties;
    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private ResetPasswordRequestRepository resetPasswordRequestRepository;

    @MockBean
    private Oauth2TokenService oauth2TokenService;

    private PasswordEncoder passwordEncoder;

    private ResetPasswordService resetPasswordService;

    private UserEntity userEntity;

    @Override
    public void init() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        resetPasswordService = new ResetPasswordService(appProperties, passwordEncoder, oauth2TokenService,
                resetPasswordRequestRepository, userEntityRepository);
        userEntity = createAndSaveUser();
    }

    @Override
    public void deleteAll() {
        resetPasswordRequestRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testSaveNewResetPasswordRequest() {
        CreateResetPasswordRequest createResetPasswordRequest = new CreateResetPasswordRequest(userEntity.getEmail());
        TokenModel tokenModel = resetPasswordService.createResetPasswordRequest(createResetPasswordRequest);
        assertThat(tokenModel).isNotNull();
        assertThat(tokenModel.getTokenId()).isNotNull();
        assertThat(tokenModel.getLogin()).isNotNull();
        assertThat(tokenModel.getEmail()).isNotNull();
        assertThat(tokenModel.getToken()).isNotNull();
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                resetPasswordRequestRepository.findById(tokenModel.getTokenId()).orElse(null);
        assertThat(resetPasswordRequestEntity).isNotNull();
        assertThat(resetPasswordRequestEntity.getExpireDate()).isNotNull();
        assertThat(resetPasswordRequestEntity.getToken()).isNotNull();
        assertThat(resetPasswordRequestEntity.getUserEntity()).isNotNull();
        assertThat(resetPasswordRequestEntity.getResetDate()).isNull();
    }

    @Test
    void testSaveResetPasswordRequestForLockedUser() {
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        CreateResetPasswordRequest createResetPasswordRequest = new CreateResetPasswordRequest(userEntity.getEmail());
        assertThrows(UserLockedException.class,
                () -> resetPasswordService.createResetPasswordRequest(createResetPasswordRequest));
    }

    @Test
    void testCreateResetPasswordRequestWithResetPasswordRequestAlreadyExistsException() {
        CreateResetPasswordRequest createResetPasswordRequest = new CreateResetPasswordRequest(userEntity.getEmail());
        resetPasswordService.createResetPasswordRequest(createResetPasswordRequest);
        assertThrows(ResetPasswordRequestAlreadyExistsException.class,
                () -> resetPasswordService.createResetPasswordRequest(createResetPasswordRequest));
    }

    @Test
    void testSaveResetPasswordRequestWithException() {
        CreateResetPasswordRequest createResetPasswordRequest = new CreateResetPasswordRequest(StringUtils.EMPTY);
        assertThrows(IllegalStateException.class,
                () -> resetPasswordService.createResetPasswordRequest(createResetPasswordRequest));
    }

    @Test
    void testResetPasswordForNotExistingToken() {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(UUID.randomUUID().toString(), PASSWORD);
        assertThrows(InvalidTokenException.class,
                () -> resetPasswordService.resetPassword(resetPasswordRequest));
    }

    @Test
    void testResetPasswordForAlreadyResetRequest() {
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                createAndSaveResetPasswordRequestEntity(LocalDateTime.now().plusMinutes(5L),
                        LocalDateTime.now().plusMinutes(2L));
        ResetPasswordRequest resetPasswordRequest =
                new ResetPasswordRequest(resetPasswordRequestEntity.getToken(), PASSWORD);
        assertThrows(InvalidTokenException.class,
                () -> resetPasswordService.resetPassword(resetPasswordRequest));
    }

    @Test
    void testResetPasswordForExpiredToken() {
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                createAndSaveResetPasswordRequestEntity(LocalDateTime.now().minusMinutes(1L), null);
        ResetPasswordRequest resetPasswordRequest =
                new ResetPasswordRequest(resetPasswordRequestEntity.getToken(), PASSWORD);
        assertThrows(InvalidTokenException.class,
                () -> resetPasswordService.resetPassword(resetPasswordRequest));
    }

    @Test
    void testResetPassword() {
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                createAndSaveResetPasswordRequestEntity(LocalDateTime.now().plusMinutes(2L), null);
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(TOKEN, NEW_PASSWORD);
        resetPasswordService.resetPassword(resetPasswordRequest);
        ResetPasswordRequestEntity actual =
                resetPasswordRequestRepository.findById(resetPasswordRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getResetDate()).isNotNull();
        assertThat(actual.getUserEntity().getPasswordChangeDate()).isNotNull();
        assertThat(actual.getUserEntity().getPassword()).isNotNull();
        verify(oauth2TokenService, atLeastOnce()).revokeTokens(any(UserEntity.class));
    }

    @Test
    void testResetPasswordShouldThrowPasswordsMatchedException() {
        createAndSaveResetPasswordRequestEntity(LocalDateTime.now().plusMinutes(2L), null);
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(TOKEN, PASSWORD);
        assertThrows(PasswordsMatchedException.class, () -> resetPasswordService.resetPassword(resetPasswordRequest));
    }

    @Test
    void testResetPasswordForLockedUser() {
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        createAndSaveResetPasswordRequestEntity(LocalDateTime.now().plusMinutes(2L), null);
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(TOKEN, PASSWORD);
        assertThrows(UserLockedException.class, () -> resetPasswordService.resetPassword(resetPasswordRequest));
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setPassword(passwordEncoder.encode(PASSWORD));
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }

    private ResetPasswordRequestEntity createAndSaveResetPasswordRequestEntity(LocalDateTime expireDate,
                                                                               LocalDateTime resetDate) {
        ResetPasswordRequestEntity resetPasswordRequestEntity = new ResetPasswordRequestEntity();
        resetPasswordRequestEntity.setToken(md5Hex(TOKEN));
        resetPasswordRequestEntity.setExpireDate(expireDate);
        resetPasswordRequestEntity.setResetDate(resetDate);
        resetPasswordRequestEntity.setUserEntity(userEntity);
        return resetPasswordRequestRepository.save(resetPasswordRequestEntity);
    }
}
