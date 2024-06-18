package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangeEmailRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.EmailAlreadyBoundException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.repository.ChangeEmailRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

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
@Import({AppProperties.class, ChangeEmailService.class})
class ChangeEmailServiceTest extends AbstractJpaTest {

    private static final String NEW_EMAIL = "newemail@mail.ru";
    private static final String INVALID_USERNAME = "abc";
    private static final String CONFIRMATION_CODE = "code";

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private ChangeEmailRequestRepository changeEmailRequestRepository;
    @Autowired
    private ChangeEmailService changeEmailService;

    private UserEntity userEntity;

    @Override
    public void init() {
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
    void testCreateChangeEmailRequestWithPreviousExpired() {
        createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().minusMinutes(appProperties.getChangeEmail().getValidityMinutes()), null);
        internalTestCreateChangeEmailRequest(NEW_EMAIL);
        assertThat(changeEmailRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void testCreateChangeEmailRequestWithPreviousConfirmed() {
        createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangeEmail().getValidityMinutes()),
                LocalDateTime.now().minusMinutes(1L));
        internalTestCreateChangeEmailRequest(NEW_EMAIL);
        assertThat(changeEmailRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void testCreateChangeEmailRequestWithChangeEmailRequestAlreadyExistsException() {
        var actual = changeEmailService.createChangeEmailRequest(userEntity.getLogin(), NEW_EMAIL);
        assertThat(actual).isNotNull();
        String username = userEntity.getLogin();
        assertThrows(ChangeEmailRequestAlreadyExistsException.class,
                () -> changeEmailService.createChangeEmailRequest(username, NEW_EMAIL));
        assertThat(changeEmailRequestRepository.count()).isOne();
    }

    @Test
    void testCreateChangeEmailRequestForNotExistingUser() {
        assertThrows(EntityNotFoundException.class,
                () -> changeEmailService.createChangeEmailRequest(INVALID_USERNAME, NEW_EMAIL));
    }

    @Test
    void testChangeEmailForNotExistingToken() {
        assertThrows(InvalidTokenException.class,
                () -> changeEmailService.confirmChangeEmail(UUID.randomUUID().toString(), CONFIRMATION_CODE));
    }

    @Test
    void testChangeEmailForAlreadyConfirmedRequest() {
        var changeEmailRequestEntity = createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangeEmail().getValidityMinutes()),
                LocalDateTime.now().minusMinutes(1L));
        assertThrows(InvalidTokenException.class,
                () -> changeEmailService.confirmChangeEmail(changeEmailRequestEntity.getToken(), CONFIRMATION_CODE));
    }

    @Test
    void testChangeEmailForExpiredToken() {
        var changeEmailRequestEntity =
                createAndSaveChangeEmailRequestEntity(LocalDateTime.now().minusDays(1L), null);
        assertThrows(InvalidTokenException.class,
                () -> changeEmailService.confirmChangeEmail(changeEmailRequestEntity.getToken(), CONFIRMATION_CODE));

    }

    @Test
    void testChangeEmail() {
        var changeEmailRequestEntity = createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangeEmail().getValidityMinutes()), null);
        changeEmailService.confirmChangeEmail(changeEmailRequestEntity.getToken(), CONFIRMATION_CODE);
        ChangeEmailRequestEntity actual =
                changeEmailRequestRepository.findById(changeEmailRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getConfirmationDate()).isNotNull();
        assertThat(actual.getUserEntity().getEmail()).isEqualTo(changeEmailRequestEntity.getNewEmail());
    }

    @Test
    void testGetChangeEmailRequestStatusForEmptyRequests() {
        testInactiveChangeEmailRequestStatus();
    }

    @Test
    void testGetActiveChangeEmailRequest() {
        var changeEmailRequestEntity = createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangeEmail().getValidityMinutes()), null);
        var changeEmailStatusDto = changeEmailService.getChangeEmailRequestStatus(userEntity.getLogin());
        assertThat(changeEmailStatusDto).isNotNull();
        assertThat(changeEmailStatusDto.isActive()).isTrue();
        assertThat(changeEmailStatusDto.getNewEmail()).isEqualTo(changeEmailRequestEntity.getNewEmail());
    }

    @Test
    void testGetChangeEmailRequestStatusForAlreadyConfirmedRequest() {
        createAndSaveChangeEmailRequestEntity(
                LocalDateTime.now().plusMinutes(appProperties.getChangeEmail().getValidityMinutes()),
                LocalDateTime.now().minusMinutes(1L));
        testInactiveChangeEmailRequestStatus();
    }

    @Test
    void testGetChangeEmailRequestStatusForExpiredToken() {
        createAndSaveChangeEmailRequestEntity(LocalDateTime.now().minusDays(1L), null);
        testInactiveChangeEmailRequestStatus();
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }

    private void testInactiveChangeEmailRequestStatus() {
        var changeEmailStatusDto = changeEmailService.getChangeEmailRequestStatus(userEntity.getLogin());
        assertThat(changeEmailStatusDto).isNotNull();
        assertThat(changeEmailStatusDto.isActive()).isFalse();
    }

    private ChangeEmailRequestEntity createAndSaveChangeEmailRequestEntity(LocalDateTime expireDate,
                                                                           LocalDateTime confirmationDate) {
        ChangeEmailRequestEntity changeEmailRequestEntity = new ChangeEmailRequestEntity();
        changeEmailRequestEntity.setConfirmationCode(md5Hex(CONFIRMATION_CODE));
        changeEmailRequestEntity.setToken(UUID.randomUUID().toString());
        changeEmailRequestEntity.setExpireDate(expireDate);
        changeEmailRequestEntity.setConfirmationDate(confirmationDate);
        changeEmailRequestEntity.setUserEntity(userEntity);
        changeEmailRequestEntity.setNewEmail(NEW_EMAIL);
        changeEmailRequestEntity.setCreated(LocalDateTime.now());
        return changeEmailRequestRepository.save(changeEmailRequestEntity);
    }

    private void internalTestCreateChangeEmailRequest(String email) {
        var actual = changeEmailService.createChangeEmailRequest(userEntity.getLogin(), email);
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
