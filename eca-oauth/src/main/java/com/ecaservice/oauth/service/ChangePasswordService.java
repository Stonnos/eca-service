package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangePasswordRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.InvalidPasswordException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.NotSafePasswordException;
import com.ecaservice.oauth.exception.PasswordsMatchedException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ChangePasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.web.dto.model.ChangePasswordRequestStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ecaservice.common.web.util.MaskUtils.mask;
import static com.ecaservice.common.web.util.RandomUtils.generateToken;
import static com.ecaservice.oauth.config.audit.AuditCodes.CONFIRM_CHANGE_PASSWORD_REQUEST;
import static com.ecaservice.oauth.config.audit.AuditCodes.CREATE_CHANGE_PASSWORD_REQUEST;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Change password service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final AppProperties appProperties;
    private final PasswordEncoder passwordEncoder;
    private final Oauth2TokenService oauth2TokenService;
    private final PasswordValidationService passwordValidationService;
    private final ChangePasswordRequestRepository changePasswordRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Create change password request.
     *
     * @param userId                - user id
     * @param changePasswordRequest - change password request
     * @return change password token model
     */
    @Audit(CREATE_CHANGE_PASSWORD_REQUEST)
    public TokenModel createChangePasswordRequest(Long userId, ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, userId));
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        if (!isValidOldPassword(userEntity, changePasswordRequest)) {
            throw new InvalidPasswordException();
        }
        if (isPasswordsMatched(userEntity, changePasswordRequest)) {
            throw new PasswordsMatchedException(userId);
        }
        var validationResult
                = passwordValidationService.validate(changePasswordRequest.getNewPassword());
        if (!validationResult.isValid()) {
            throw new NotSafePasswordException(validationResult.getDetails());
        }
        LocalDateTime now = LocalDateTime.now();
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(userEntity,
                        now);
        if (changePasswordRequestEntity != null) {
            throw new ChangePasswordRequestAlreadyExistsException(userId);
        }
        String token = generateToken();
        LocalDateTime expireDate = now.plusMinutes(appProperties.getChangePassword().getValidityMinutes());
        changePasswordRequestEntity = saveChangePasswordRequest(changePasswordRequest, userEntity, token, expireDate);
        return TokenModel.builder()
                .token(token)
                .tokenId(changePasswordRequestEntity.getId())
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .build();
    }

    /**
     * Change user password.
     *
     * @param token - token value
     */
    @Audit(value = CONFIRM_CHANGE_PASSWORD_REQUEST)
    @Transactional
    public ChangePasswordRequestEntity changePassword(String token) {
        log.info("Starting to change password for token [{}]", mask(token));
        String md5Hash = md5Hex(token);
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findByTokenAndExpireDateAfterAndConfirmationDateIsNull(md5Hash,
                        LocalDateTime.now()).orElseThrow(InvalidTokenException::new);
        UserEntity userEntity = changePasswordRequestEntity.getUserEntity();
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        userEntity.setPassword(changePasswordRequestEntity.getNewPassword());
        userEntity.setPasswordChangeDate(LocalDateTime.now());
        changePasswordRequestEntity.setConfirmationDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        changePasswordRequestRepository.save(changePasswordRequestEntity);
        oauth2TokenService.revokeTokens(userEntity);
        log.info("New password has been set for user [{}], change password request id [{}]", userEntity.getId(),
                changePasswordRequestEntity.getId());
        return changePasswordRequestEntity;
    }

    /**
     * Gets change password request status for specified user.
     *
     * @param userId - user id
     * @return change password request status dto
     */
    public ChangePasswordRequestStatusDto getChangePasswordRequestStatus(Long userId) {
        log.info("Gets change password request status for user [{}]", userId);
        var userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, userId));
        var changePasswordRequestEntity =
                changePasswordRequestRepository.findByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(userEntity,
                        LocalDateTime.now());
        if (changePasswordRequestEntity == null) {
            log.info("No one active change password request has been found for user [{}]", userId);
            return ChangePasswordRequestStatusDto.builder()
                    .active(false)
                    .build();
        } else {
            log.info("Active change password request [{}] has been found for user [{}]",
                    changePasswordRequestEntity.getId(), userId);
            return ChangePasswordRequestStatusDto.builder()
                    .active(true)
                    .build();
        }
    }

    private boolean isValidOldPassword(UserEntity userEntity, ChangePasswordRequest changePasswordRequest) {
        return passwordEncoder.matches(changePasswordRequest.getOldPassword().trim(), userEntity.getPassword());
    }

    private boolean isPasswordsMatched(UserEntity userEntity, ChangePasswordRequest changePasswordRequest) {
        return passwordEncoder.matches(changePasswordRequest.getNewPassword().trim(), userEntity.getPassword());
    }

    private ChangePasswordRequestEntity saveChangePasswordRequest(ChangePasswordRequest changePasswordRequest,
                                                                  UserEntity userEntity,
                                                                  String token,
                                                                  LocalDateTime expireDate) {
        var changePasswordRequestEntity = new ChangePasswordRequestEntity();
        changePasswordRequestEntity.setToken(md5Hex(token));
        changePasswordRequestEntity.setExpireDate(expireDate);
        String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword().trim());
        changePasswordRequestEntity.setNewPassword(encodedPassword);
        changePasswordRequestEntity.setUserEntity(userEntity);
        return changePasswordRequestRepository.save(changePasswordRequestEntity);
    }
}
