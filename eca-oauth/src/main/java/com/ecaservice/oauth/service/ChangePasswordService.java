package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.config.ChangePasswordConfig;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangePasswordRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.InvalidPasswordException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ChangePasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ecaservice.oauth.util.Utils.generateToken;
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

    private final ChangePasswordConfig changePasswordConfig;
    private final PasswordEncoder passwordEncoder;
    private final Oauth2TokenService oauth2TokenService;
    private final ChangePasswordRequestRepository changePasswordRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Create change password request.
     *
     * @param userId                - user id
     * @param changePasswordRequest - change password request
     * @return change password token model
     */
    public TokenModel createChangePasswordRequest(Long userId, ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, userId));
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        if (!isValidOldPassword(userEntity, changePasswordRequest)) {
            throw new InvalidPasswordException();
        }
        LocalDateTime now = LocalDateTime.now();
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(userEntity,
                        now);
        if (changePasswordRequestEntity != null) {
            throw new ChangePasswordRequestAlreadyExistsException(userId);
        }
        changePasswordRequestEntity = new ChangePasswordRequestEntity();
        String token = generateToken();
        changePasswordRequestEntity.setToken(md5Hex(token));
        changePasswordRequestEntity.setExpireDate(now.plusMinutes(changePasswordConfig.getValidityMinutes()));
        String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword().trim());
        changePasswordRequestEntity.setNewPassword(encodedPassword);
        changePasswordRequestEntity.setUserEntity(userEntity);
        changePasswordRequestRepository.save(changePasswordRequestEntity);
        return new TokenModel(token, userId, changePasswordRequestEntity.getId());
    }

    /**
     * Change user password.
     *
     * @param token - token value
     */
    @Transactional
    public void changePassword(String token) {
        String md5Hash = md5Hex(token);
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findByTokenAndExpireDateAfterAndConfirmationDateIsNull(md5Hash,
                        LocalDateTime.now()).orElseThrow(() -> new InvalidTokenException(token));
        UserEntity userEntity = changePasswordRequestEntity.getUserEntity();
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        userEntity.setPassword(changePasswordRequestEntity.getNewPassword());
        userEntity.setPasswordDate(LocalDateTime.now());
        changePasswordRequestEntity.setConfirmationDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        changePasswordRequestRepository.save(changePasswordRequestEntity);
        oauth2TokenService.revokeTokens(userEntity);
        log.info("New password has been set for user [{}], change password request id [{}]", userEntity.getId(),
                changePasswordRequestEntity.getId());
    }

    private boolean isValidOldPassword(UserEntity userEntity, ChangePasswordRequest changePasswordRequest) {
        return passwordEncoder.matches(changePasswordRequest.getOldPassword().trim(), userEntity.getPassword());
    }
}
