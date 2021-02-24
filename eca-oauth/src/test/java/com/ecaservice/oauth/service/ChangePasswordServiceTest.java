package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.ChangePasswordConfig;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangePasswordRequestAlreadyExistsException;
import com.ecaservice.oauth.repository.ChangePasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ChangePasswordService} functionality.
 *
 * @author Roman Batygin
 */
@Import(ChangePasswordConfig.class)
class ChangePasswordServiceTest extends AbstractJpaTest {

    private static final String NEW_PASSWORD = "mewPassword";
    private static final long INVALID_USER_ID = 1000L;

    @Inject
    private ChangePasswordConfig changePasswordConfig;
    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private ChangePasswordRequestRepository changePasswordRequestRepository;

    private ChangePasswordService changePasswordService;

    private UserEntity userEntity;

    @Override
    public void init() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        changePasswordService =
                new ChangePasswordService(changePasswordConfig, passwordEncoder, changePasswordRequestRepository,
                        userEntityRepository);
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
    void testCreateChangePasswordRequestWithPreviousExpired() {
        createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().minusDays(changePasswordConfig.getValidityMinutes()), null);
        internalTestCreateChangePasswordRequest();
        assertThat(changePasswordRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void testCreateChangePasswordRequestWithPreviousApproved() {
        createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().plusMinutes(changePasswordConfig.getValidityMinutes()),
                LocalDateTime.now().minusMinutes(1L));
        internalTestCreateChangePasswordRequest();
        assertThat(changePasswordRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void tesCreateChangePasswordRequestWithException() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(userEntity.getPassword(), NEW_PASSWORD);
        ChangePasswordRequestEntity actual =
                changePasswordService.createChangePasswordRequest(userEntity.getId(), changePasswordRequest);
        assertThat(actual).isNotNull();
        assertThrows(ChangePasswordRequestAlreadyExistsException.class,
                () -> changePasswordService.createChangePasswordRequest(userEntity.getId(), changePasswordRequest));
        assertThat(changePasswordRequestRepository.count()).isOne();
    }


    @Test
    void testCreateChangePasswordRequestForNotExistingUser() {
        assertThrows(EntityNotFoundException.class,
                () -> changePasswordService.createChangePasswordRequest(INVALID_USER_ID, new ChangePasswordRequest()));
    }

    /*@Test
    void testResetPasswordForNotExistingToken() {
        createAndSaveUser();
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(UUID.randomUUID().toString(), PASSWORD);
        assertThrows(IllegalStateException.class,
                () -> changePasswordService.resetPassword(resetPasswordRequest));
    }

    @Test
    void testResetPasswordForAlreadyResetRequest() {
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                createAndSaveResetPasswordRequestEntity(LocalDateTime.now().plusMinutes(5L),
                        LocalDateTime.now().plusMinutes(2L));
        ResetPasswordRequest resetPasswordRequest =
                new ResetPasswordRequest(resetPasswordRequestEntity.getToken(), PASSWORD);
        assertThrows(IllegalStateException.class,
                () -> changePasswordService.resetPassword(resetPasswordRequest));
    }

    @Test
    void testResetPasswordForExpiredToken() {
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                createAndSaveResetPasswordRequestEntity(LocalDateTime.now().minusMinutes(1L), null);
        ResetPasswordRequest resetPasswordRequest =
                new ResetPasswordRequest(resetPasswordRequestEntity.getToken(), PASSWORD);
        assertThrows(IllegalStateException.class,
                () -> changePasswordService.resetPassword(resetPasswordRequest));
    }

    @Test
    void testResetPassword() {
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                createAndSaveResetPasswordRequestEntity(LocalDateTime.now().plusMinutes(2L), null);
        ResetPasswordRequest resetPasswordRequest =
                new ResetPasswordRequest(resetPasswordRequestEntity.getToken(), PASSWORD);
        changePasswordService.resetPassword(resetPasswordRequest);
        ResetPasswordRequestEntity actual =
                changePasswordRequestRepository.findById(resetPasswordRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getResetDate()).isNotNull();
        assertThat(actual.getUserEntity().getPassword()).isNotNull();
    }*/

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }

    private ChangePasswordRequestEntity createAndSaveChangePasswordRequestEntity(LocalDateTime expireDate,
                                                                                 LocalDateTime approveDate) {
        String token = UUID.randomUUID().toString();
        ChangePasswordRequestEntity changePasswordRequestEntity = new ChangePasswordRequestEntity();
        changePasswordRequestEntity.setToken(token);
        changePasswordRequestEntity.setExpireDate(expireDate);
        changePasswordRequestEntity.setApproveDate(approveDate);
        changePasswordRequestEntity.setUserEntity(userEntity);
        changePasswordRequestEntity.setNewPassword(NEW_PASSWORD);
        return changePasswordRequestRepository.save(changePasswordRequestEntity);
    }

    private void internalTestCreateChangePasswordRequest() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(userEntity.getPassword(), NEW_PASSWORD);
        ChangePasswordRequestEntity actual =
                changePasswordService.createChangePasswordRequest(userEntity.getId(), changePasswordRequest);
        assertThat(actual).isNotNull();
        assertThat(actual.getExpireDate()).isNotNull();
        assertThat(actual.getToken()).isNotNull();
        assertThat(actual.getUserEntity()).isNotNull();
        assertThat(actual.getNewPassword()).isNotNull();
    }
}
