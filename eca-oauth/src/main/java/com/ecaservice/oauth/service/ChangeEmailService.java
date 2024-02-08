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
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ChangeEmailRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.web.dto.model.ChangeEmailRequestStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.common.web.util.MaskUtils.mask;
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
    private final RandomValueStringGenerator generator = new RandomValueStringGenerator();
    private final ChangeEmailRequestRepository changeEmailRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Initialization method.
     */
    @PostConstruct
    public void initialize() {
        this.generator.setLength(appProperties.getChangeEmail().getConfirmationCodeLength());
    }

    /**
     * Creates change email request.
     *
     * @param userId   - user id
     * @param newEmail - new email
     * @return token model
     */
    @Audit(CREATE_CHANGE_EMAIL_REQUEST)
    public TokenModel createChangeEmailRequest(Long userId, String newEmail) {
        log.info("Starting to create change email request for user [{}], new email [{}]", userId, maskEmail(newEmail));
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, userId));
        if (Objects.equals(userEntity.getEmail(), newEmail)) {
            throw new EmailAlreadyBoundException(userId);
        }
        if (userEntityRepository.existsByEmail(newEmail)) {
            throw new EmailDuplicationException(userId);
        }
        LocalDateTime now = LocalDateTime.now();
        if (changeEmailRequestRepository
                .existsByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(userEntity, now)) {
            throw new ChangeEmailRequestAlreadyExistsException(userId);
        }
        String confirmationCode = generator.generate();
        LocalDateTime expireDate = now.plusMinutes(appProperties.getChangeEmail().getValidityMinutes());
        var changeEmailRequestEntity = saveChangeEmailRequest(newEmail, userEntity, confirmationCode, expireDate);
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
     * @param userId - user id
     * @return change email request status dto
     */
    public ChangeEmailRequestStatusDto getChangeEmailRequestStatus(Long userId) {
        log.info("Gets change email request status for user [{}]", userId);
        var userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, userId));
        var changeEmailRequestOpt = getLastActiveChangeEmailRequest(userEntity);
        if (changeEmailRequestOpt.isEmpty()) {
            log.info("No one active change email request has been found for user [{}]", userId);
            return ChangeEmailRequestStatusDto.builder()
                    .active(false)
                    .build();
        } else {
            var changeEmailRequest = changeEmailRequestOpt.get();
            log.info("Active change email request [{}] has been found for user [{}]", changeEmailRequest.getToken(),
                    userId);
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
                                                            String confirmationCode,
                                                            LocalDateTime expireDate) {
        var changeEmailRequestEntity = new ChangeEmailRequestEntity();
        changeEmailRequestEntity.setToken(UUID.randomUUID().toString());
        changeEmailRequestEntity.setConfirmationCode(md5Hex(confirmationCode));
        changeEmailRequestEntity.setExpireDate(expireDate);
        changeEmailRequestEntity.setNewEmail(newEmail);
        changeEmailRequestEntity.setUserEntity(userEntity);
        return changeEmailRequestRepository.save(changeEmailRequestEntity);
    }
}
