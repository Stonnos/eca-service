package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.oauth.config.ChangeEmailConfig;
import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.EmailAlreadyBoundException;
import com.ecaservice.oauth.exception.ChangeEmailRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.EmailDuplicationException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ChangeEmailRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.ecaservice.common.web.util.MaskUtils.mask;
import static com.ecaservice.common.web.util.RandomUtils.generateToken;
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

    private final ChangeEmailConfig changeEmailConfig;
    private final ChangeEmailRequestRepository changeEmailRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Creates change email request.
     *
     * @param userId   - user id
     * @param newEmail - new email
     * @return token model
     */
    @Audit(CREATE_CHANGE_EMAIL_REQUEST)
    public TokenModel createChangeEmailRequest(Long userId, String newEmail) {
        log.info("Starting to create change email request for user [{}]", userId);
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, userId));
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
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
        String token = generateToken();
        LocalDateTime expireDate = now.plusHours(changeEmailConfig.getValidityHours());
        var changeEmailRequestEntity = saveChangeEmailRequest(newEmail, userEntity, token, expireDate);
        return TokenModel.builder()
                .token(token)
                .tokenId(changeEmailRequestEntity.getId())
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .build();
    }

    /**
     * Change user email.
     *
     * @param token - token value
     */
    @Audit(value = CONFIRM_CHANGE_EMAIL_REQUEST, targetInitiator = "userEntity.login")
    @Transactional
    public ChangeEmailRequestEntity changeEmail(String token) {
        log.info("Starting to change email for token [{}]", mask(token));
        String md5Hash = md5Hex(token);
        var changeEmailRequestEntity =
                changeEmailRequestRepository.findByTokenAndExpireDateAfterAndConfirmationDateIsNull(md5Hash,
                        LocalDateTime.now())
                        .orElseThrow(InvalidTokenException::new);
        UserEntity userEntity = changeEmailRequestEntity.getUserEntity();
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        userEntity.setEmail(changeEmailRequestEntity.getNewEmail());
        changeEmailRequestEntity.setConfirmationDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        changeEmailRequestRepository.save(changeEmailRequestEntity);
        log.info("New email has been set for user [{}], change email request id [{}]", userEntity.getId(),
                changeEmailRequestEntity.getId());
        return changeEmailRequestEntity;
    }

    private ChangeEmailRequestEntity saveChangeEmailRequest(String newEmail,
                                                            UserEntity userEntity,
                                                            String token,
                                                            LocalDateTime expireDate) {
        var changeEmailRequestEntity = new ChangeEmailRequestEntity();
        changeEmailRequestEntity.setToken(md5Hex(token));
        changeEmailRequestEntity.setExpireDate(expireDate);
        changeEmailRequestEntity.setNewEmail(newEmail);
        changeEmailRequestEntity.setUserEntity(userEntity);
        return changeEmailRequestRepository.save(changeEmailRequestEntity);
    }
}
