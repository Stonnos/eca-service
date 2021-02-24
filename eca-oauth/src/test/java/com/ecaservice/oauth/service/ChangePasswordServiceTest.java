package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.ChangePasswordConfig;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangePasswordRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.InvalidTokenException;
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

    @Test
    void testChangePasswordForNotExistingToken() {
        String token = UUID.randomUUID().toString();
        assertThrows(InvalidTokenException.class, () -> changePasswordService.changePassword(token));
    }

    @Test
    void testChangePasswordForAlreadyApprovedRequest() {
        ChangePasswordRequestEntity changePasswordRequestEntity = createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().plusMinutes(changePasswordConfig.getValidityMinutes()),
                LocalDateTime.now().minusMinutes(1L));
        String token = changePasswordRequestEntity.getToken();
        assertThrows(InvalidTokenException.class, () -> changePasswordService.changePassword(token));
    }

    @Test
    void testChangePasswordForExpiredToken() {
        ChangePasswordRequestEntity changePasswordRequestEntity =
                createAndSaveChangePasswordRequestEntity(LocalDateTime.now().minusDays(1L), null);
        String token = changePasswordRequestEntity.getToken();
        assertThrows(InvalidTokenException.class, () -> changePasswordService.changePassword(token));
    }

    @Test
    void testChangePassword() {
        ChangePasswordRequestEntity changePasswordRequestEntity = createAndSaveChangePasswordRequestEntity(
                LocalDateTime.now().plusMinutes(changePasswordConfig.getValidityMinutes()), null);
        changePasswordService.changePassword(changePasswordRequestEntity.getToken());
        ChangePasswordRequestEntity actual =
                changePasswordRequestRepository.findById(changePasswordRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getApproveDate()).isNotNull();
        assertThat(actual.getUserEntity().getPassword()).isEqualTo(changePasswordRequestEntity.getNewPassword());
    }

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
