package com.ecaservice.oauth.service;

import com.ecaservice.oauth.dto.ForgotPasswordRequest;
import com.ecaservice.oauth.entity.ResetPasswordRequest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Reset password service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final ResetPasswordRequestRepository resetPasswordRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Gets active reset password request or save new if not exists.
     *
     * @param forgotPasswordRequest - forgot password request
     * @return reset password request
     */
    public ResetPasswordRequest getOrSaveResetPasswordRequest(ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity userEntity = userEntityRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(
                () -> new IllegalStateException(
                        String.format("Can't create reset password request, because user with email %s doesn't exists!",
                                forgotPasswordRequest.getEmail())));
        LocalDateTime now = LocalDateTime.now();
        ResetPasswordRequest resetPasswordRequest =
                resetPasswordRequestRepository.findByUserEntityAndExpireDateAfterAndResetDateIsNull(userEntity, now);
        if (resetPasswordRequest == null) {
            resetPasswordRequest = new ResetPasswordRequest();
            resetPasswordRequest.setToken("");
            resetPasswordRequest.setExpireDate(null);
            resetPasswordRequest.setUserEntity(userEntity);
            resetPasswordRequestRepository.save(resetPasswordRequest);
        }
        return resetPasswordRequest;
    }
}
