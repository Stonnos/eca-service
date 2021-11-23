package com.ecaservice.oauth.controller;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.oauth.dto.CreateResetPasswordRequest;
import com.ecaservice.oauth.dto.ResetPasswordRequest;
import com.ecaservice.oauth.event.model.PasswordResetNotificationEvent;
import com.ecaservice.oauth.event.model.ResetPasswordRequestNotificationEvent;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.service.ResetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.ecaservice.oauth.controller.doc.ApiExamples.CREATE_RESET_PASSWORD_REQUEST_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.INVALID_TOKEN_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.RESET_PASSWORD_REQUEST_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.USER_EMAIL_RESPONSE_JSON;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Implements reset password REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Reset password API")
@Validated
@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ResetPasswordRequestRepository resetPasswordRequestRepository;

    /**
     * Creates reset password request.
     *
     * @param createResetPasswordRequest - reset password request
     */
    @Operation(
            description = "Creates reset password request",
            summary = "Creates reset password request",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = CREATE_RESET_PASSWORD_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = USER_EMAIL_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/create-reset-request")
    public void createResetPasswordRequest(@Valid @RequestBody CreateResetPasswordRequest createResetPasswordRequest) {
        log.info("Received reset password request creation [{}]", createResetPasswordRequest);
        var tokenModel = resetPasswordService.createResetPasswordRequest(createResetPasswordRequest);
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
            summary = "Verify reset password token",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = "false"),
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/verify-token")
    public boolean verifyToken(
            @Size(max = MAX_LENGTH_255)
            @Parameter(description = "Reset password token", required = true) @RequestParam String token) {
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
            summary = "Reset password with specified token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = RESET_PASSWORD_REQUEST_JSON)
                    })
            }),
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
    @PostMapping(value = "/reset")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("Received reset password request");
        var resetPasswordRequestEntity = resetPasswordService.resetPassword(resetPasswordRequest);
        applicationEventPublisher.publishEvent(
                new PasswordResetNotificationEvent(this, resetPasswordRequestEntity.getUserEntity()));
    }
}
