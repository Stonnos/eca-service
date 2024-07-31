package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.oauth.event.model.ChangeEmailRequestEmailEvent;
import com.ecaservice.oauth.event.model.EmailChangedEmailEvent;
import com.ecaservice.oauth.service.ChangeEmailService;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
     * @param principal - principal object
     * @param newEmail  - new email
     * @return change email requests status dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Creates change email request",
            summary = "Creates change email request",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ChangeEmailStatusResponse",
                                                    ref = "#/components/examples/ChangeEmailStatusResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ChangeEmailRequestStatusDto.class)
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
    public ChangeEmailRequestStatusDto createChangeEmailRequest(Principal principal,
                                                                @Parameter(description = "User email", required = true)
                                                                @Email(regexp = EMAIL_REGEX)
                                                                @Size(min = VALUE_1, max = EMAIL_MAX_SIZE) @RequestParam
                                                                String newEmail) {
        log.info("Received change email request for user [{}]", principal.getName());
        var tokenModel = changeEmailService.createChangeEmailRequest(principal.getName(), newEmail);
        applicationEventPublisher.publishEvent(new ChangeEmailRequestEmailEvent(this, tokenModel, newEmail));
        log.info("Change email request [{}] has been processed for user [{}]", tokenModel.getToken(),
                principal.getName());
        return ChangeEmailRequestStatusDto.builder()
                .token(tokenModel.getToken())
                .active(true)
                .newEmail(newEmail)
                .build();
    }

    /**
     * Confirms change email request.
     *
     * @param token            - token value
     * @param confirmationCode - confirmation code
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
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
            @Parameter(description = "Token value", required = true) @RequestParam String token,
            @Size(min = VALUE_1, max = MAX_LENGTH_255)
            @Parameter(description = "Confirmation code", required = true) @RequestParam String confirmationCode) {
        log.info("Received change email request [{}] confirmation", token);
        var changeEmailRequest = changeEmailService.confirmChangeEmail(token, confirmationCode);
        applicationEventPublisher.publishEvent(
                new EmailChangedEmailEvent(this, changeEmailRequest.getUserEntity(), changeEmailRequest));
        log.info("Change email request [{}] confirmation has been processed", token);
    }

    /**
     * Gets change email request status.
     *
     * @param principal - principal object
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets change email request status",
            summary = "Gets change email request status",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ChangeEmailStatusResponse",
                                                    ref = "#/components/examples/ChangeEmailStatusResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ChangeEmailRequestStatusDto.class)
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
    public ChangeEmailRequestStatusDto getChangeEmailRequestStatus(Principal principal) {
        log.info("Received get change email request status for user [{}]", principal.getName());
        return changeEmailService.getChangeEmailRequestStatus(principal.getName());
    }
}
