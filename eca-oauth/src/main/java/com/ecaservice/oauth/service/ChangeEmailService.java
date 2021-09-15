package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
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

import static com.ecaservice.common.web.util.RandomUtils.generateToken;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Change password service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeEmailService {

    private final ChangeEmailRequestRepository changeEmailRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Creates change email request.
     *
     * @param userId   - user id
     * @param newEmail - new email
     * @return token model
     */
    public TokenModel createChangeEmailRequest(Long userId, String newEmail) {
        log.info("Starting to create change email request for user [{}]", userId);
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, userId));
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        String emailToUpdate = newEmail.trim();
        if (!userEntity.getEmail().equals(emailToUpdate) && userEntityRepository.existsByEmail(emailToUpdate)) {
            throw new EmailDuplicationException(userId);
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (changeEmailRequestRepository
                    .existsByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(userEntity, now)) {
                throw new ChangeEmailRequestAlreadyExistsException(userId);
            }
            String token = generateToken();
            LocalDateTime expireDate = now.plusMinutes(1L);
            var changeEmailRequestEntity = saveChangeEmailRequest(emailToUpdate, userEntity, token, expireDate);
            return TokenModel.builder()
                    .token(token)
                    .tokenId(changeEmailRequestEntity.getId())
                    .login(userEntity.getLogin())
                    .email(userEntity.getEmail())
                    .build();
        }
    }

    /**
     * Change user email.
     *
     * @param token - token value
     */
    @Transactional
    public ChangeEmailRequestEntity changeEmail(String token) {
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
