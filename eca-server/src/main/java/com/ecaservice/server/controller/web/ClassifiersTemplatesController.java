package com.ecaservice.server.controller.web;

import com.ecaservice.server.service.classifiers.ClassifiersTemplateService;
import com.ecaservice.web.dto.model.FormTemplateDto;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.web.dto.doc.CommonApiExamples.UNAUTHORIZED_RESPONSE_JSON;

/**
 * Implements API to manage classifiers templates.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API to manage classifiers templates")
@Validated
@RestController
@RequestMapping("/classifiers/templates")
@RequiredArgsConstructor
public class ClassifiersTemplatesController {

    private final ClassifiersTemplateService classifiersTemplateService;

    //TODO Добавить пример ответа в open api
    //TODO обновить документацию api

    /**
     * Gets classifiers form templates.
     *
     * @return form templates list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets classifiers form templates",
            summary = "Gets classifiers form templates",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = ""),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = FormTemplateDto.class))
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/list")
    public List<FormTemplateDto> getClassifiersTemplates() {
        log.info("Request classifiers templates");
        return classifiersTemplateService.getClassifiersTemplates();
    }
}
