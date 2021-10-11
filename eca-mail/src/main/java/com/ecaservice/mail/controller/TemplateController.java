package com.ecaservice.mail.controller;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.mail.mapping.TemplateMapper;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.service.TemplateService;
import com.ecaservice.web.dto.model.EmailTemplateDto;
import com.ecaservice.web.dto.model.EmailTemplatesPageDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.mail.controller.docs.ApiExamples.EMAIL_TEMPLATES_PAGE_RESPONSE_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.INVALID_PAGE_REQUEST_RESPONSE_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.SIMPLE_PAGE_REQUEST_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.UNAUTHORIZED_RESPONSE_JSON;

/**
 * Email template API for web application.
 *
 * @author Roman Batygin
 */
@Validated
@Slf4j
@Tag(name = "Email template API for web application")
@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;
    private final TemplateMapper templateMapper;

    /**
     * Finds email templates with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return email templates tables page
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @Operation(
            description = "Finds email templates with specified options such as filter, sorting and paging",
            summary = "Finds email templates with specified options such as filter, sorting and paging",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = SIMPLE_PAGE_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = EMAIL_TEMPLATES_PAGE_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = EmailTemplatesPageDto.class)
                            )
                    ),
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
                                            @ExampleObject(value = INVALID_PAGE_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/list")
    public PageDto<EmailTemplateDto> getTemplatesPage(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received templates page request: {}", pageRequestDto);
        Page<TemplateEntity> templatesPage = templateService.getNextPage(pageRequestDto);
        List<EmailTemplateDto> templateDtoList = templateMapper.mapTemplates(templatesPage.getContent());
        return PageDto.of(templateDtoList, pageRequestDto.getPage(), templatesPage.getTotalElements());
    }
}
