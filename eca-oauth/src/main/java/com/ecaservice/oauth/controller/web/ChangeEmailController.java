package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.oauth.event.model.ChangeEmailRequestNotificationEvent;
import com.ecaservice.oauth.event.model.EmailChangedNotificationEvent;
import com.ecaservice.oauth.service.ChangeEmailService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.ChangeEmailRequestStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Implements change email REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Change email API")
@Validated
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UniqueEmailErrorResponse",
                                                    ref = "#/components/examples/UniqueEmailErrorResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/request")
    public void createChangeEmailRequest(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @Parameter(description = "User email", required = true)
                                         @Email(regexp = EMAIL_REGEX)
                                         @Size(min = VALUE_1, max = EMAIL_MAX_SIZE) @RequestParam String newEmail) {
        log.info("Received change email request for user [{}]", userDetails.getId());
        var tokenModel = changeEmailService.createChangeEmailRequest(userDetails.getId(), newEmail);
        log.info("Change email request [{}] has been created for user [{}]", tokenModel.getTokenId(),
                userDetails.getId());
        applicationEventPublisher.publishEvent(new ChangeEmailRequestNotificationEvent(this, tokenModel, newEmail));
    }

    /**
     * Confirms change email request.
     *
     * @param token - token value
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Confirms change email request",
            summary = "Confirms change email request",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InvalidTokenErrorCode",
                                                    ref = "#/components/examples/InvalidTokenErrorCode"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/confirm")
    public void confirmChangeEmailRequest(
            @Size(min = VALUE_1, max = MAX_LENGTH_255)
            @Parameter(description = "Token value", required = true) @RequestParam String token) {
        log.info("Received change email request confirmation");
        var changeEmailRequest = changeEmailService.changeEmail(token);
        applicationEventPublisher.publishEvent(
                new EmailChangedNotificationEvent(this, changeEmailRequest.getUserEntity()));
    }

    /**
     * Gets change email request status.
     *
     * @param userDetails - user details
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets change email request status",
            summary = "Gets change email request status",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/request-status")
    public ChangeEmailRequestStatusDto getChangeEmailRequestStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Received get change email request status for user [{}]", userDetails.getId());
        return changeEmailService.getChangeEmailRequestStatus(userDetails.getId());
    }
}
