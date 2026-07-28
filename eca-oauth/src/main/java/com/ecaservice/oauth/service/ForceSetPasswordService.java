package com.ecaservice.oauth.service;

import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.ForceSetPasswordRequest;
import com.ecaservice.oauth.entity.ForceSetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.InvalidConfirmationCodeException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.NotSafePasswordException;
import com.ecaservice.oauth.exception.PasswordsMatchedException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ForceSetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static com.ecaservice.common.web.util.MaskUtils.mask;
import static com.ecaservice.oauth.config.audit.AuditCodes.FORCE_SET_PASSWORD;
import static com.ecaservice.oauth.util.RandomUtils.randomString;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Set password service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ForceSetPasswordService {

    private static final int TOKEN_LENGTH = 96;

    private final StringKeyGenerator tokenGenerator =
            new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), TOKEN_LENGTH);

    private final AppProperties appProperties;
    private final Oauth2RevokeTokenService oauth2RevokeTokenService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidationService passwordValidationService;
    private final ForceSetPasswordRequestRepository forceSetPasswordRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Create force set password request.
     *
     * @param userEntity - user entity
     * @return set password request token
     */
    @Transactional
    public TokenModel createForceSetPasswordRequest(UserEntity userEntity) {
        log.info("Starting to create force set password request for user [{}]", userEntity.getLogin());
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        forceSetPasswordRequestRepository.deleteActiveRequests(userEntity, LocalDateTime.now());
        var requestEntity = new ForceSetPasswordRequestEntity();
        String token = tokenGenerator.generateKey();
        String confirmationCode = randomString(appProperties.getForceSetPassword().getConfirmationCodeLength());
        requestEntity.setToken(md5Hex(token));
        requestEntity.setConfirmationCode(md5Hex(confirmationCode));
        requestEntity.setExpireDate(
                LocalDateTime.now().plusMinutes(appProperties.getForceSetPassword().getValidityMinutes()));
        requestEntity.setUserEntity(userEntity);
        requestEntity.setCreated(LocalDateTime.now());
        forceSetPasswordRequestRepository.save(requestEntity);
        log.info("Force set password request [{}] has been created for user [{}]", requestEntity.getId(),
                userEntity.getLogin());
        return TokenModel.builder()
                .token(token)
                .confirmationCode(confirmationCode)
                .tokenId(requestEntity.getId())
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .build();
    }

    /**
     * Force set password.
     *
     * @param forceSetPasswordRequest - force set password request
     */
    @Audit(value = FORCE_SET_PASSWORD, initiatorKey = "#result.userEntity.login")
    @Transactional
    public ForceSetPasswordRequestEntity forceSetPassword(ForceSetPasswordRequest forceSetPasswordRequest) {
        log.info("Starting to force set password for token [{}]", mask(forceSetPasswordRequest.getToken()));
        var requestEntity = getRequestByToken(forceSetPasswordRequest.getToken())
                .orElseThrow(InvalidTokenException::new);
        UserEntity userEntity = requestEntity.getUserEntity();
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        if (isPasswordsMatched(userEntity, forceSetPasswordRequest)) {
            throw new PasswordsMatchedException();
        }
        var validationResult
                = passwordValidationService.validate(forceSetPasswordRequest.getPassword());
        if (!validationResult.isValid()) {
            throw new NotSafePasswordException(validationResult.getDetails());
        }
        String confirmationCode = forceSetPasswordRequest.getConfirmationCode();
        String confirmationCodeMd5Hash = md5Hex(confirmationCode);
        if (!requestEntity.getConfirmationCode().equals(confirmationCodeMd5Hash)) {
            throw new InvalidConfirmationCodeException();
        }
        userEntity.setPassword(passwordEncoder.encode(forceSetPasswordRequest.getPassword().trim()));
        userEntity.setPasswordChangeDate(LocalDateTime.now());
        userEntity.setForceChangePassword(false);
        requestEntity.setPasswordDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        forceSetPasswordRequestRepository.save(requestEntity);
        oauth2RevokeTokenService.revokeTokens(userEntity);
        log.info("New password has been force set for user [{}], set password request id [{}]", userEntity.getId(),
                requestEntity.getId());
        return requestEntity;
    }

    /**
     * Verify set password token.
     *
     * @param token - set password token
     * @return {@code true} if token is valid (not expired and not reset). {@code false} otherwise
     */
    public boolean verifyToken(String token) {
        log.info("Received request for set password token [{}] verification", mask(token));
        String md5Hash = md5Hex(token);
        boolean verified =
                forceSetPasswordRequestRepository.existsByTokenAndExpireDateAfterAndPasswordDateIsNull(md5Hash,
                        LocalDateTime.now());
        log.info("Set password request token [{}] verification result: {}", mask(token), verified);
        return verified;
    }

    private Optional<ForceSetPasswordRequestEntity> getRequestByToken(String token) {
        String md5HashToken = md5Hex(token);
        return forceSetPasswordRequestRepository.findByTokenAndExpireDateAfterAndPasswordDateIsNull(md5HashToken,
                LocalDateTime.now());
    }

    private boolean isPasswordsMatched(UserEntity userEntity, ForceSetPasswordRequest forceSetPasswordRequest) {
        return passwordEncoder.matches(forceSetPasswordRequest.getPassword().trim(), userEntity.getPassword());
    }
}
