package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.service.ChangeEmailService;
import com.ecaservice.user.model.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_REGEX;

/**
 * Implements change email REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Change email API")
@RestController
@RequestMapping("/email/change")
@RequiredArgsConstructor
public class ChangeEmailController {

    private final ChangeEmailService changeEmailService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Creates change email request.
     *
     * @param userDetails - user details
     * @param newEmail    - new email
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Creates change email request",
            summary = "Creates change email request",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PostMapping(value = "/request")
    public void createChangeEmailRequest(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @Parameter(description = "User email", required = true)
                                         @Email(regexp = EMAIL_REGEX)
                                         @Size(max = EMAIL_MAX_SIZE) @RequestParam String newEmail) {
        log.info("Received change email request for user [{}]", userDetails.getId());
        var tokenModel = changeEmailService.createChangeEmailRequest(userDetails.getId(), newEmail);
        log.info("Change email request [{}] has been created for user [{}]", tokenModel.getTokenId(),
                userDetails.getId());
        //applicationEventPublisher.publishEvent(new ChangePasswordNotificationEvent(this, tokenModel));
    }

    /**
     * Confirms change email request.
     *
     * @param token - token value
     */
    @Operation(
            description = "Confirms change email request",
            summary = "Confirms change email request"
    )
    @PostMapping(value = "/confirm")
    public void confirmChangeEmailRequest(
            @Parameter(description = "Token value", required = true) @RequestParam String token) {
        log.info("Received change email request confirmation");
        changeEmailService.changeEmail(token);
    }
}
