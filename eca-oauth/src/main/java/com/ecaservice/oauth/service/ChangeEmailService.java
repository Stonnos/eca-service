package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangeEmailRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.EmailAlreadyBoundException;
import com.ecaservice.oauth.exception.EmailDuplicationException;
import com.ecaservice.oauth.exception.InvalidConfirmationCodeException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ChangeEmailRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.web.dto.model.ChangeEmailRequestStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.common.web.util.MaskUtils.maskEmail;
import static com.ecaservice.oauth.config.audit.AuditCodes.CONFIRM_CHANGE_EMAIL_REQUEST;
import static com.ecaservice.oauth.config.audit.AuditCodes.CREATE_CHANGE_EMAIL_REQUEST;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Change email service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeEmailService {

    private final AppProperties appProperties;
    private final ChangeEmailRequestRepository changeEmailRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Creates change email request.
     *
     * @param user     - user id
     * @param newEmail - new email
     * @return token model
     */
    @Audit(CREATE_CHANGE_EMAIL_REQUEST)
    public TokenModel createChangeEmailRequest(String user, String newEmail) {
        String token = UUID.randomUUID().toString();
        putMdc(TX_ID, token);
        log.info("Starting to create change email request for user [{}], new email [{}]", user, maskEmail(newEmail));
        UserEntity userEntity = userEntityRepository.findByLogin(user)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, user));
        if (Objects.equals(userEntity.getEmail(), newEmail)) {
            throw new EmailAlreadyBoundException();
        }
        if (userEntityRepository.existsByEmail(newEmail)) {
            throw new EmailDuplicationException();
        }
        LocalDateTime now = LocalDateTime.now();
        if (changeEmailRequestRepository
                .existsByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(userEntity, now)) {
            throw new ChangeEmailRequestAlreadyExistsException();
        }
        String confirmationCode =
                RandomStringUtils.random(appProperties.getChangeEmail().getConfirmationCodeLength(), false, true);
        LocalDateTime expireDate = now.plusMinutes(appProperties.getChangeEmail().getValidityMinutes());
        var changeEmailRequestEntity =
                saveChangeEmailRequest(newEmail, userEntity, token, confirmationCode, expireDate);
        log.info("Change email request [{}] has been created for user [{}], new email [{}]",
                changeEmailRequestEntity.getToken(), userEntity.getId(), maskEmail(newEmail));
        return TokenModel.builder()
                .token(changeEmailRequestEntity.getToken())
                .tokenId(changeEmailRequestEntity.getId())
                .confirmationCode(confirmationCode)
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .build();
    }

    /**
     * Gets change email request status for specified user.
     *
     * @param user - username
     * @return change email request status dto
     */
    public ChangeEmailRequestStatusDto getChangeEmailRequestStatus(String user) {
        log.info("Gets change email request status for user [{}]", user);
        var userEntity = userEntityRepository.findByLogin(user)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, user));
        var changeEmailRequestOpt = getLastActiveChangeEmailRequest(userEntity);
        if (changeEmailRequestOpt.isEmpty()) {
            log.info("No one active change email request has been found for user [{}]", user);
            return ChangeEmailRequestStatusDto.builder()
                    .active(false)
                    .build();
        } else {
            var changeEmailRequest = changeEmailRequestOpt.get();
            log.info("Active change email request [{}] has been found for user [{}]", changeEmailRequest.getToken(),
                    user);
            return ChangeEmailRequestStatusDto.builder()
                    .active(true)
                    .token(changeEmailRequest.getToken())
                    .newEmail(changeEmailRequest.getNewEmail())
                    .build();
        }
    }

    /**
     * Confirm change user email.
     *
     * @param token            - token value
     * @param confirmationCode - confirmation code
     */
    @Audit(value = CONFIRM_CHANGE_EMAIL_REQUEST)
    @Transactional
    public ChangeEmailRequestEntity confirmChangeEmail(String token, String confirmationCode) {
        putMdc(TX_ID, token);
        log.info("Starting to confirm change email for token [{}]", token);
        var changeEmailRequestEntity =
                changeEmailRequestRepository.findByTokenAndExpireDateAfterAndConfirmationDateIsNull(token,
                                LocalDateTime.now())
                        .orElseThrow(InvalidTokenException::new);
        String confirmationCodeMd5Hash = md5Hex(confirmationCode);
        if (!changeEmailRequestEntity.getConfirmationCode().equals(confirmationCodeMd5Hash)) {
            throw new InvalidConfirmationCodeException();
        }
        UserEntity userEntity = changeEmailRequestEntity.getUserEntity();
        userEntity.setEmail(changeEmailRequestEntity.getNewEmail());
        changeEmailRequestEntity.setConfirmationDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        changeEmailRequestRepository.save(changeEmailRequestEntity);
        log.info("New email [{}] has been set for user [{}], change email request id [{}]",
                maskEmail(userEntity.getEmail()), userEntity.getId(), changeEmailRequestEntity.getToken());
        return changeEmailRequestEntity;
    }

    private Optional<ChangeEmailRequestEntity> getLastActiveChangeEmailRequest(UserEntity userEntity) {
        LocalDateTime now = LocalDateTime.now();
        return changeEmailRequestRepository.findByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(userEntity,
                now);
    }

    private ChangeEmailRequestEntity saveChangeEmailRequest(String newEmail,
                                                            UserEntity userEntity,
                                                            String token,
                                                            String confirmationCode,
                                                            LocalDateTime expireDate) {
        var changeEmailRequestEntity = new ChangeEmailRequestEntity();
        changeEmailRequestEntity.setToken(UUID.randomUUID().toString());
        changeEmailRequestEntity.setToken(token);
        changeEmailRequestEntity.setConfirmationCode(md5Hex(confirmationCode));
        changeEmailRequestEntity.setExpireDate(expireDate);
        changeEmailRequestEntity.setNewEmail(newEmail);
        changeEmailRequestEntity.setUserEntity(userEntity);
        changeEmailRequestEntity.setCreated(LocalDateTime.now());
        return changeEmailRequestRepository.save(changeEmailRequestEntity);
    }
}
