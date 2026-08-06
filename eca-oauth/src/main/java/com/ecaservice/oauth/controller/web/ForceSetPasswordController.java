package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.oauth.dto.ForceSetPasswordRequest;
import com.ecaservice.oauth.event.model.PasswordChangedEmailEvent;
import com.ecaservice.oauth.service.ForceSetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ecaservice.common.web.util.MaskUtils.mask;

/**
 * Implements force set password REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Force set password API")
@Validated
@RestController
@RequestMapping("/force-set-password")
@RequiredArgsConstructor
public class ForceSetPasswordController {

    private final ForceSetPasswordService forceSetPasswordService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Force set password with specified token.
     *
     * @param forceSetPasswordRequest - reset password request
     */
    @Operation(
            description = "Force set password with specified token",
            summary = "Force set password with specified token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "ForceSetPasswordRequest",
                                    ref = "#/components/examples/ForceSetPasswordRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
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
    @PostMapping
    public void forceSetPassword(@Valid @RequestBody ForceSetPasswordRequest forceSetPasswordRequest) {
        log.info("Received force set password request for token [{}]", mask(forceSetPasswordRequest.getToken()));
        var requestEntity = forceSetPasswordService.forceSetPassword(forceSetPasswordRequest);
        applicationEventPublisher.publishEvent(new PasswordChangedEmailEvent(this, requestEntity.getUserEntity()));
        log.info("Force set password request has been processed for token [{}]",
                mask(forceSetPasswordRequest.getToken()));
    }
}
