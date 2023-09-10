package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.CreateResetPasswordRequest;
import com.ecaservice.oauth.dto.ResetPasswordRequest;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.exception.NotSafePasswordException;
import com.ecaservice.oauth.exception.PasswordsMatchedException;
import com.ecaservice.oauth.exception.ResetPasswordRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ecaservice.common.web.util.MaskUtils.mask;
import static com.ecaservice.common.web.util.RandomUtils.generateToken;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Reset password service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final AppProperties appProperties;
    private final PasswordEncoder passwordEncoder;
    private final Oauth2TokenService oauth2TokenService;
    private final PasswordValidationService passwordValidationService;
    private final ResetPasswordRequestRepository resetPasswordRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Save reset password request.
     *
     * @param createResetPasswordRequest - reset password request data
     * @return reset password request token
     */
    public TokenModel createResetPasswordRequest(CreateResetPasswordRequest createResetPasswordRequest) {
        UserEntity userEntity = userEntityRepository.findByEmail(createResetPasswordRequest.getEmail())
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't create reset password request, because user with email %s doesn't exists!",
                                createResetPasswordRequest.getEmail())));
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        LocalDateTime now = LocalDateTime.now();
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                resetPasswordRequestRepository.findByUserEntityAndExpireDateAfterAndResetDateIsNull(userEntity, now);
        if (resetPasswordRequestEntity != null) {
            throw new ResetPasswordRequestAlreadyExistsException(userEntity.getId());
        }
        resetPasswordRequestEntity = new ResetPasswordRequestEntity();
        String token = generateToken();
        resetPasswordRequestEntity.setToken(md5Hex(token));
        resetPasswordRequestEntity.setExpireDate(
                now.plusMinutes(appProperties.getResetPassword().getValidityMinutes()));
        resetPasswordRequestEntity.setUserEntity(userEntity);
        resetPasswordRequestRepository.save(resetPasswordRequestEntity);
        return TokenModel.builder()
                .token(token)
                .tokenId(resetPasswordRequestEntity.getId())
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .build();
    }

    /**
     * Reset password.
     *
     * @param resetPasswordRequest - reset password request
     */
    @Transactional
    public ResetPasswordRequestEntity resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("Starting to reset password for token [{}]", mask(resetPasswordRequest.getToken()));
        String md5Hash = md5Hex(resetPasswordRequest.getToken());
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                resetPasswordRequestRepository.findByTokenAndExpireDateAfterAndResetDateIsNull(md5Hash,
                        LocalDateTime.now())
                        .orElseThrow(InvalidTokenException::new);
        UserEntity userEntity = resetPasswordRequestEntity.getUserEntity();
        if (userEntity.isLocked()) {
            throw new UserLockedException(userEntity.getId());
        }
        if (isPasswordsMatched(userEntity, resetPasswordRequest)) {
            throw new PasswordsMatchedException(userEntity.getId());
        }
        var validationResult
                = passwordValidationService.validate(resetPasswordRequest.getPassword());
        if (!validationResult.isValid()) {
            throw new NotSafePasswordException(validationResult.getDetails());
        }
        userEntity.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword().trim()));
        userEntity.setPasswordChangeDate(LocalDateTime.now());
        userEntity.setForceChangePassword(false);
        resetPasswordRequestEntity.setResetDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        resetPasswordRequestRepository.save(resetPasswordRequestEntity);
        oauth2TokenService.revokeTokens(userEntity);
        log.info("New password has been set for user [{}], reset password request id [{}]", userEntity.getId(),
                resetPasswordRequestEntity.getId());
        return resetPasswordRequestEntity;
    }

    private boolean isPasswordsMatched(UserEntity userEntity, ResetPasswordRequest resetPasswordRequest) {
        return passwordEncoder.matches(resetPasswordRequest.getPassword().trim(), userEntity.getPassword());
    }
}
