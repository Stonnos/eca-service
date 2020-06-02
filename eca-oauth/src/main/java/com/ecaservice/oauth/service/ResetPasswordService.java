package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.dto.ForgotPasswordRequest;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.ecaservice.oauth.util.Utils.generateToken;

/**
 * Reset password service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final ResetPasswordConfig resetPasswordConfig;
    private final ResetPasswordRequestRepository resetPasswordRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Gets active reset password request or save new if not exists.
     *
     * @param forgotPasswordRequest - forgot password request
     * @return reset password request
     */
    public ResetPasswordRequestEntity getOrSaveResetPasswordRequest(ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity userEntity = userEntityRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(
                () -> new IllegalStateException(
                        String.format("Can't create reset password request, because user with email %s doesn't exists!",
                                forgotPasswordRequest.getEmail())));
        LocalDateTime now = LocalDateTime.now();
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                resetPasswordRequestRepository.findByUserEntityAndExpireDateAfterAndResetDateIsNull(userEntity, now);
        if (resetPasswordRequestEntity == null) {
            resetPasswordRequestEntity = new ResetPasswordRequestEntity();
            resetPasswordRequestEntity.setToken(generateToken(userEntity));
            resetPasswordRequestEntity.setExpireDate(now.plusMinutes(resetPasswordConfig.getValidityMinutes()));
            resetPasswordRequestEntity.setUserEntity(userEntity);
            resetPasswordRequestRepository.save(resetPasswordRequestEntity);
        }
        return resetPasswordRequestEntity;
    }
}
