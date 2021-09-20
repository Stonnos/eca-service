package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.ChangeEmailConfig;
import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangeEmailRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.EmailAlreadyBoundException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.repository.ChangeEmailRequestRepository;
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
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ChangeEmailService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ChangeEmailConfig.class, ChangeEmailService.class})
class ChangeEmailServiceTest extends AbstractJpaTest {

    private static final String NEW_EMAIL = "newemail@mail.ru";
    private static final long INVALID_USER_ID = 1000L;
    private static final String PASSWORD = "pa66word!";
    private static final String TOKEN = "token";

    @Inject
    private ChangeEmailConfig changeEmailConfig;
    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private ChangeEmailRequestRepository changeEmailRequestRepository;
    @Inject
    private ChangeEmailService changeEmailService;

    private PasswordEncoder passwordEncoder;
    private UserEntity userEntity;

    @Override
    public void init() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        userEntity = createAndSaveUser();
    }

    @Override
    public void deleteAll() {
        changeEmailRequestRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testCreateChangeEmailRequest() {
        internalTestCreateChangeEmailRequest(NEW_EMAIL);
        assertThat(changeEmailRequestRepository.count()).isOne();
    }

    @Test
    void testCreateChangeEmailRequestWithSameEmailValue() {
        assertThrows(EmailAlreadyBoundException.class,
                () -> internalTestCreateChangeEmailRequest(userEntity.getEmail()));
        assertThat(changeEmailRequestRepository.count()).isZero();
    }

    @Test
    void testCreateChangeEmailRequestForLockedUser() {
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        assertThrows(UserLockedException.class, () -> internalTestCreateChangeEmailRequest(NEW_EMAIL));
    }

    @Test
    void testCreateChangeEmailRequestWithPreviousExpired() {
        createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().minusDays(changeEmailConfig.getValidityHours()), null);
        internalTestCreateChangeEmailRequest(NEW_EMAIL);
        assertThat(changeEmailRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void testCreateChangeEmailRequestWithPreviousConfirmed() {
        createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(changeEmailConfig.getValidityHours()),
                LocalDateTime.now().minusMinutes(1L));
        internalTestCreateChangeEmailRequest(NEW_EMAIL);
        assertThat(changeEmailRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void testCreateChangeEmailRequestWithChangeEmailRequestAlreadyExistsException() {
        var actual = changeEmailService.createChangeEmailRequest(userEntity.getId(), NEW_EMAIL);
        assertThat(actual).isNotNull();
        Long userId = userEntity.getId();
        assertThrows(ChangeEmailRequestAlreadyExistsException.class,
                () -> changeEmailService.createChangeEmailRequest(userId, NEW_EMAIL));
        assertThat(changeEmailRequestRepository.count()).isOne();
    }

    @Test
    void testCreateChangeEmailRequestForNotExistingUser() {
        assertThrows(EntityNotFoundException.class,
                () -> changeEmailService.createChangeEmailRequest(INVALID_USER_ID, NEW_EMAIL));
    }

    @Test
    void testChangeEmailForNotExistingToken() {
        String token = UUID.randomUUID().toString();
        assertThrows(InvalidTokenException.class, () -> changeEmailService.changeEmail(token));
    }

    @Test
    void testChangeEmailForAlreadyConfirmedRequest() {
        var changeEmailRequestEntity = createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(changeEmailConfig.getValidityHours()),
                LocalDateTime.now().minusMinutes(1L));
        String token = changeEmailRequestEntity.getToken();
        assertThrows(InvalidTokenException.class, () -> changeEmailService.changeEmail(token));
    }

    @Test
    void testChangeEmailForExpiredToken() {
        var changeEmailRequestEntity =
                createAndSaveChangeEmailRequestEntity(LocalDateTime.now().minusDays(1L), null);
        String token = changeEmailRequestEntity.getToken();
        assertThrows(InvalidTokenException.class, () -> changeEmailService.changeEmail(token));
    }

    @Test
    void testChangeEmail() {
        var changeEmailRequestEntity = createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(changeEmailConfig.getValidityHours()), null);
        changeEmailService.changeEmail(TOKEN);
        ChangeEmailRequestEntity actual =
                changeEmailRequestRepository.findById(changeEmailRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getConfirmationDate()).isNotNull();
        assertThat(actual.getUserEntity().getEmail()).isEqualTo(changeEmailRequestEntity.getNewEmail());
    }

    @Test
    void testChangeEmailForLockedUser() {
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(changeEmailConfig.getValidityHours()), null);
        assertThrows(UserLockedException.class, () -> changeEmailService.changeEmail(TOKEN));
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setEmail(passwordEncoder.encode(PASSWORD));
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }

    private ChangeEmailRequestEntity createAndSaveChangeEmailRequestEntity(LocalDateTime expireDate,
                                                                           LocalDateTime confirmationDate) {
        ChangeEmailRequestEntity changeEmailRequestEntity = new ChangeEmailRequestEntity();
        changeEmailRequestEntity.setToken(md5Hex(TOKEN));
        changeEmailRequestEntity.setExpireDate(expireDate);
        changeEmailRequestEntity.setConfirmationDate(confirmationDate);
        changeEmailRequestEntity.setUserEntity(userEntity);
        changeEmailRequestEntity.setNewEmail(NEW_EMAIL);
        return changeEmailRequestRepository.save(changeEmailRequestEntity);
    }

    private void internalTestCreateChangeEmailRequest(String email) {
        var actual = changeEmailService.createChangeEmailRequest(userEntity.getId(), email);
        assertThat(actual).isNotNull();
        assertThat(actual.getToken()).isNotNull();
        assertThat(actual.getLogin()).isNotNull();
        assertThat(actual.getEmail()).isNotNull();
        assertThat(actual.getTokenId()).isNotNull();
        var changeEmailRequestEntity = changeEmailRequestRepository.findById(actual.getTokenId())
                .orElse(null);
        assertThat(changeEmailRequestEntity).isNotNull();
        assertThat(changeEmailRequestEntity.getExpireDate()).isNotNull();
        assertThat(changeEmailRequestEntity.getUserEntity()).isNotNull();
        assertThat(changeEmailRequestEntity.getNewEmail()).isEqualTo(NEW_EMAIL);
    }
}
