package com.ecaservice.mail.controller;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.mail.mapping.EmailRequestMapper;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.service.EmailService;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Implements REST API for email application.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API for email sending")
@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final EmailRequestMapper emailRequestMapper;

    /**
     * Saves email request.
     *
     * @param emailRequest - email request
     * @return email response
     */
    @Operation(
            description = "Saves email request to sent",
            summary = "Saves email request to sent",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "EmailRequest",
                                    ref = "#/components/examples/EmailRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EmailResponse",
                                                    ref = "#/components/examples/EmailResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = EmailResponse.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InvalidTemplateCodeResponse",
                                                    ref = "#/components/examples/InvalidTemplateCodeResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/email-request")
    public EmailResponse saveRequest(@Valid @RequestBody EmailRequest emailRequest) {
        log.debug("Received email request: {}", emailRequest);
        Email email = emailService.saveEmail(emailRequest);
        return emailRequestMapper.map(email);
    }
}
