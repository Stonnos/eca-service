package com.ecaservice.oauth.controller;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.event.model.ChangePasswordRequestNotificationEvent;
import com.ecaservice.oauth.event.model.PasswordChangedNotificationEvent;
import com.ecaservice.oauth.service.ChangePasswordService;
import com.ecaservice.user.model.UserDetailsImpl;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.oauth.controller.doc.ApiExamples.CHANGE_PASSWORD_REQUEST_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.INVALID_PASSWORD_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.INVALID_TOKEN_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.UNAUTHORIZED_RESPONSE_JSON;

/**
 * Implements change password REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Change password API")
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = CHANGE_PASSWORD_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = INVALID_PASSWORD_RESPONSE_JSON),
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
    @Operation(
            description = "Confirms change password request",
            summary = "Confirms change password request",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = INVALID_TOKEN_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/confirm")
    public void confirmChangePasswordRequest(
            @Parameter(description = "Token value", required = true) @RequestParam String token) {
        log.info("Received change password request confirmation");
        var changePasswordRequest = changePasswordService.changePassword(token);
        applicationEventPublisher.publishEvent(
                new PasswordChangedNotificationEvent(this, changePasswordRequest.getUserEntity()));
    }
}
