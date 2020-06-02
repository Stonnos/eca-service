package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.ForgotPasswordRequest;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.service.ResetPasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                resetPasswordService.getOrSaveResetPasswordRequest(forgotPasswordRequest);
        log.info("Reset password request [{}] has been created for user with email [{}]",
                resetPasswordRequestEntity.getId(), forgotPasswordRequest.getEmail());
    }
}
