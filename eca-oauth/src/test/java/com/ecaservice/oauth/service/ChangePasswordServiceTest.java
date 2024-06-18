package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangePasswordRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.InvalidConfirmationCodeException;
import com.ecaservice.oauth.exception.InvalidPasswordException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.PasswordsMatchedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ChangePasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
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
 * Unit tests for checking {@link ChangePasswordService} functionality.
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, PasswordRuleHandler.class, PasswordValidationService.class, ObjectMapper.class})
class ChangePasswordServiceTest extends AbstractJpaTest {

    private static final String NEW_PASSWORD = "#123dCgrh56$f";
    private static final String INVALID_USERNAME = "abc";
    private static final String PASSWORD = "pa66word!";
    private static final String CONFIRMATION_CODE = "code";

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private PasswordValidationService passwordValidationService;
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private ChangePasswordRequestRepository changePasswordRequestRepository;

    @MockBean
    private Oauth2RevokeTokenService oauth2RevokeTokenService;

    private ChangePasswordService changePasswordService;

    private PasswordEncoder passwordEncoder;

    private UserEntity userEntity;

    @Override
    public void init() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        changePasswordService = new ChangePasswordService(appProperties, passwordEncoder, oauth2RevokeTokenService,
                passwordValidationService, changePasswordRequestRepository, userEntityRepository);
        userEntity = createAndSaveUser();
    }

    @Override
    public void deleteAll() {
        changePasswordRequestRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testCreateChangePasswordRequest() {
        internalTestCreateChangePasswordRequest();
        assertThat(changePasswordRequestRepository.count()).isOne();
    }

    @Test
    void testCreateChangePasswordRequestShouldThrowPasswordsMatchedException() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, PASSWORD);
        assertThrows(PasswordsMatchedException.class,
                () -> changePasswordService.createChangePasswordRequest(userEntity.getLogin(), changePasswordRequest));
    }

    @Test
    void testCreateChangePasswordRequestWithPreviousExpired() {
        createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().minusDays(appProperties.getChangePassword().getValidityMinutes()), null);
        internalTestCreateChangePasswordRequest();
        assertThat(changePasswordRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void testCreateChangePasswordRequestWithPreviousConfirmed() {
        createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangePassword().getValidityMinutes()),
                LocalDateTime.now().minusMinutes(1L));
        internalTestCreateChangePasswordRequest();
        assertThat(changePasswordRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void testCreateChangePasswordRequestWithChangePasswordRequestAlreadyExistsException() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, NEW_PASSWORD);
        TokenModel actual =
                changePasswordService.createChangePasswordRequest(userEntity.getLogin(), changePasswordRequest);
        assertThat(actual).isNotNull();
        assertThrows(ChangePasswordRequestAlreadyExistsException.class,
                () -> changePasswordService.createChangePasswordRequest(userEntity.getLogin(), changePasswordRequest));
        assertThat(changePasswordRequestRepository.count()).isOne();
    }

    @Test
    void testCreateChangePasswordRequestWithInvalidPassword() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(NEW_PASSWORD, NEW_PASSWORD);
        assertThrows(InvalidPasswordException.class,
                () -> changePasswordService.createChangePasswordRequest(userEntity.getLogin(), changePasswordRequest));
    }

    @Test
    void testCreateChangePasswordRequestForNotExistingUser() {
        assertThrows(EntityNotFoundException.class,
                () -> changePasswordService.createChangePasswordRequest(INVALID_USERNAME, new ChangePasswordRequest()));
    }

    @Test
    void testConfirmChangePasswordForNotExistingToken() {
        String token = UUID.randomUUID().toString();
        assertThrows(InvalidTokenException.class,
                () -> changePasswordService.confirmChangePassword(token, CONFIRMATION_CODE));
    }

    @Test
    void testConfirmChangePasswordForAlreadyConfirmedRequest() {
        ChangePasswordRequestEntity changePasswordRequestEntity = createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangePassword().getValidityMinutes()),
                LocalDateTime.now().minusMinutes(1L));
        assertThrows(InvalidTokenException.class,
                () -> changePasswordService.confirmChangePassword(changePasswordRequestEntity.getToken(),
                        CONFIRMATION_CODE));
    }

    @Test
    void testConfirmChangePasswordForExpiredToken() {
        ChangePasswordRequestEntity changePasswordRequestEntity =
                createAndSaveChangePasswordRequestEntity(LocalDateTime.now().minusDays(1L), null);
        assertThrows(InvalidTokenException.class,
                () -> changePasswordService.confirmChangePassword(changePasswordRequestEntity.getToken(),
                        CONFIRMATION_CODE));
    }

    @Test
    void testConfirmChangePassword() {
        ChangePasswordRequestEntity changePasswordRequestEntity = createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangePassword().getValidityMinutes()), null);
        changePasswordService.confirmChangePassword(changePasswordRequestEntity.getToken(), CONFIRMATION_CODE);
        ChangePasswordRequestEntity actual =
                changePasswordRequestRepository.findById(changePasswordRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getConfirmationDate()).isNotNull();
        assertThat(actual.getUserEntity().getPasswordChangeDate()).isNotNull();
        assertThat(actual.getUserEntity().getPassword()).isEqualTo(changePasswordRequestEntity.getNewPassword());
        verify(oauth2RevokeTokenService, atLeastOnce()).revokeTokens(any(UserEntity.class));
    }

    @Test
    void testConfirmChangePasswordWithInvalidConfirmationCode() {
        ChangePasswordRequestEntity changePasswordRequestEntity = createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangePassword().getValidityMinutes()), null);
        assertThrows(InvalidConfirmationCodeException.class,
                () -> changePasswordService.confirmChangePassword(changePasswordRequestEntity.getToken(), "code1"));
    }

    @Test
    void testGetChangePasswordRequestStatusWithPreviousExpired() {
        createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().minusDays(appProperties.getChangePassword().getValidityMinutes()), null);
        internalTestGetNotActiveChangePasswordRequestStatus();
    }

    @Test
    void testGetChangePasswordRequestStatusWithPreviousConfirmed() {
        createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangePassword().getValidityMinutes()),
                LocalDateTime.now().minusMinutes(1L));
        internalTestGetNotActiveChangePasswordRequestStatus();
    }

    @Test
    void testGetChangePasswordRequestStatusWithEmptyRequests() {
        internalTestGetNotActiveChangePasswordRequestStatus();
    }

    @Test
    void testGetActiveChangePasswordRequestStatus() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, NEW_PASSWORD);
        changePasswordService.createChangePasswordRequest(userEntity.getLogin(), changePasswordRequest);
        var changePasswordRequestStatus = changePasswordService.getChangePasswordRequestStatus(userEntity.getLogin());
        assertThat(changePasswordRequestStatus).isNotNull();
        assertThat(changePasswordRequestStatus.isActive()).isTrue();
    }

    private void internalTestGetNotActiveChangePasswordRequestStatus() {
        var changePasswordRequestStatus = changePasswordService.getChangePasswordRequestStatus(userEntity.getLogin());
        assertThat(changePasswordRequestStatus).isNotNull();
        assertThat(changePasswordRequestStatus.isActive()).isFalse();
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setPassword(passwordEncoder.encode(PASSWORD));
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }

    private ChangePasswordRequestEntity createAndSaveChangePasswordRequestEntity(LocalDateTime expireDate,
                                                                                 LocalDateTime confirmationDate) {
        ChangePasswordRequestEntity changePasswordRequestEntity = new ChangePasswordRequestEntity();
        changePasswordRequestEntity.setConfirmationCode(md5Hex(CONFIRMATION_CODE));
        changePasswordRequestEntity.setToken(UUID.randomUUID().toString());
        changePasswordRequestEntity.setExpireDate(expireDate);
        changePasswordRequestEntity.setConfirmationDate(confirmationDate);
        changePasswordRequestEntity.setUserEntity(userEntity);
        changePasswordRequestEntity.setNewPassword(NEW_PASSWORD);
        changePasswordRequestEntity.setCreated(LocalDateTime.now());
        return changePasswordRequestRepository.save(changePasswordRequestEntity);
    }

    private void internalTestCreateChangePasswordRequest() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, NEW_PASSWORD);
        TokenModel actual =
                changePasswordService.createChangePasswordRequest(userEntity.getLogin(), changePasswordRequest);
        assertThat(actual).isNotNull();
        assertThat(actual.getToken()).isNotNull();
        assertThat(actual.getLogin()).isNotNull();
        assertThat(actual.getEmail()).isNotNull();
        assertThat(actual.getTokenId()).isNotNull();
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findById(actual.getTokenId()).orElse(null);
        assertThat(changePasswordRequestEntity).isNotNull();
        assertThat(changePasswordRequestEntity.getExpireDate()).isNotNull();
        assertThat(changePasswordRequestEntity.getUserEntity()).isNotNull();
        assertThat(changePasswordRequestEntity.getNewPassword()).isNotNull();
    }
}
