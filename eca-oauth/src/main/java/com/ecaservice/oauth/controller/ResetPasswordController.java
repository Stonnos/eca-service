package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.ForgotPasswordRequest;
import com.ecaservice.oauth.dto.ResetPasswordRequest;
import com.ecaservice.oauth.event.model.ResetPasswordRequestNotificationEvent;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.service.ResetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Implements reset password REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Reset password API")
@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ResetPasswordRequestRepository resetPasswordRequestRepository;

    /**
     * Creates forgot password request.
     *
     * @param forgotPasswordRequest - forgot password request
     */
    @Operation(
            description = "Creates forgot password request",
            summary = "Creates forgot password request"
    )
    @PostMapping(value = "/forgot")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Received forgot password request {}", forgotPasswordRequest);
        var tokenModel = resetPasswordService.createResetPasswordRequest(forgotPasswordRequest);
        log.info("Reset password request [{}] has been created for user [{}]", tokenModel.getTokenId(),
                tokenModel.getLogin());
        applicationEventPublisher.publishEvent(new ResetPasswordRequestNotificationEvent(this, tokenModel));
    }

    /**
     * Verify reset password token.
     *
     * @param token - reset password token
     * @return {@code true} if token is valid (not expired and not reset). {@code false} otherwise
     */
    @Operation(
            description = "Verify reset password token",
            summary = "Verify reset password token"
    )
    @PostMapping(value = "/verify-token")
    public boolean verifyToken(@Parameter(description = "Reset password token", required = true) @RequestParam String token) {
        log.info("Received request for reset password token verification");
        String md5Hash = md5Hex(token);
        return resetPasswordRequestRepository.existsByTokenAndExpireDateAfterAndResetDateIsNull(md5Hash,
                LocalDateTime.now());
    }

    /**
     * Reset password with specified token.
     *
     * @param resetPasswordRequest - reset password request
     */
    @Operation(
            description = "Reset password with specified token",
            summary = "Reset password with specified token"
    )
    @PostMapping(value = "/reset")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("Received reset password request");
        resetPasswordService.resetPassword(resetPasswordRequest);
    }
}
