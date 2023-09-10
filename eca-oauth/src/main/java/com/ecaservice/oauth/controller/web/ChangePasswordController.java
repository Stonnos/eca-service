package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.event.model.ChangePasswordRequestNotificationEvent;
import com.ecaservice.oauth.event.model.PasswordChangedNotificationEvent;
import com.ecaservice.oauth.service.ChangePasswordService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.ChangePasswordRequestStatusDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Implements change password REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Change password API")
@Validated
@RestController
@RequestMapping("/password/change")
@RequiredArgsConstructor
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Creates change password request.
     *
     * @param userDetails           - user details
     * @param changePasswordRequest - change password request
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Creates change password request",
            summary = "Creates change password request",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "ChangePasswordRequest",
                                    ref = "#/components/examples/ChangePasswordRequest"
                            )
                    })
            }),
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
                                                    name = "NotSafePasswordErrorResponse",
                                                    ref = "#/components/examples/NotSafePasswordErrorResponse"
                                            ),
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/request")
    public void createChangePasswordRequest(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Received change password request for user [{}]", userDetails.getId());
        var tokenModel = changePasswordService.createChangePasswordRequest(userDetails.getId(), changePasswordRequest);
        log.info("Change password request [{}] has been created for user [{}]", tokenModel.getTokenId(),
                userDetails.getId());
        applicationEventPublisher.publishEvent(new ChangePasswordRequestNotificationEvent(this, tokenModel));
    }

    /**
     * Confirms change password request.
     *
     * @param token - token value
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Confirms change password request",
            summary = "Confirms change password request",
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
    public void confirmChangePasswordRequest(
            @Size(min = VALUE_1, max = MAX_LENGTH_255)
            @Parameter(description = "Token value", required = true) @RequestParam String token) {
        log.info("Received change password request confirmation");
        var changePasswordRequest = changePasswordService.changePassword(token);
        applicationEventPublisher.publishEvent(
                new PasswordChangedNotificationEvent(this, changePasswordRequest.getUserEntity()));
    }

    /**
     * Gets change password request status.
     *
     * @param userDetails - user details
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets change password request status",
            summary = "Gets change password request status",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ChangePasswordStatusResponse",
                                                    ref = "#/components/examples/ChangePasswordStatusResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ChangePasswordRequestStatusDto.class)
                            )
                    ),
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
    public ChangePasswordRequestStatusDto getChangePasswordRequestStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Received get change password request status for user [{}]", userDetails.getId());
        return changePasswordService.getChangePasswordRequestStatus(userDetails.getId());
    }
}
