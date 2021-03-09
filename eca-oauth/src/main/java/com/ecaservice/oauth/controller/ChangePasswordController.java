package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.event.model.ChangePasswordNotificationEvent;
import com.ecaservice.oauth.service.ChangePasswordService;
import com.ecaservice.oauth.service.Oauth2TokenService;
import com.ecaservice.user.model.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Implements change password REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Api(tags = "Change password API")
@RestController
@RequestMapping("/password/change")
@RequiredArgsConstructor
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;
    private final Oauth2TokenService oauth2TokenService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Creates change password request.
     *
     * @param userDetails           - user details
     * @param changePasswordRequest - change password request
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Creates change password request",
            notes = "Creates change password request"
    )
    @PostMapping(value = "/request")
    public void createChangePasswordRequest(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Received change password request for user [{}]", userDetails.getId());
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordService.createChangePasswordRequest(userDetails.getId(), changePasswordRequest);
        log.info("Change password request [{}] has been created for user [{}]",
                changePasswordRequestEntity.getId(), userDetails.getId());
        applicationEventPublisher.publishEvent(new ChangePasswordNotificationEvent(this, changePasswordRequestEntity));
    }

    /**
     * Confirms change password request.
     *
     * @param token - token value
     */
    @ApiOperation(
            value = "Confirms change password request",
            notes = "Confirms change password request"
    )
    @PostMapping(value = "/confirm")
    public void confirmChangePasswordRequest(
            @ApiParam(value = "Token value", required = true) @RequestParam String token) {
        log.info("Received change password request confirmation");
        ChangePasswordRequestEntity changePasswordRequestEntity = changePasswordService.changePassword(token);
        oauth2TokenService.revokeTokens(changePasswordRequestEntity.getUserEntity());
    }
}
