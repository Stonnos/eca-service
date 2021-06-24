package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.ForgotPasswordRequest;
import com.ecaservice.oauth.dto.ResetPasswordRequest;
import com.ecaservice.oauth.event.model.ResetPasswordNotificationEvent;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.service.ResetPasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "Reset password API")
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
    @ApiOperation(
            value = "Creates forgot password request",
            notes = "Creates forgot password request"
    )
    @PostMapping(value = "/forgot")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Received forgot password request {}", forgotPasswordRequest);
        var tokenModel = resetPasswordService.createResetPasswordRequest(forgotPasswordRequest);
        log.info("Reset password request [{}] has been created for user [{}]", tokenModel.getTokenId(),
                tokenModel.getLogin());
        applicationEventPublisher.publishEvent(new ResetPasswordNotificationEvent(this, tokenModel));
    }

    /**
     * Verify reset password token.
     *
     * @param token - reset password token
     * @return {@code true} if token is valid (not expired and not reset). {@code false} otherwise
     */
    @ApiOperation(
            value = "Verify reset password token",
            notes = "Verify reset password token"
    )
    @PostMapping(value = "/verify-token")
    public boolean verifyToken(@ApiParam(value = "Reset password token", required = true) @RequestParam String token) {
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
    @ApiOperation(
            value = "Reset password with specified token",
            notes = "Reset password with specified token"
    )
    @PostMapping(value = "/reset")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("Received reset password request");
        resetPasswordService.resetPassword(resetPasswordRequest);
    }
}
