package com.ecaservice.server.controller.web;

import com.ecaservice.server.dto.ClassifierGroupTemplatesType;
import com.ecaservice.server.service.classifiers.ClassifiersFormTemplateProvider;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

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

    private final ClassifiersFormTemplateProvider classifiersFormTemplateProvider;

    /**
     * Gets classifiers form templates.
     *
     * @param classifierGroupTemplatesType - classifier group templates type
     * @return form templates list
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets classifiers form templates",
            summary = "Gets classifiers form templates",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ClassifiersTemplatesResponse",
                                                    ref = "#/components/examples/ClassifiersTemplatesResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = FormTemplateGroupDto.class))
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/list")
    public List<FormTemplateGroupDto> getClassifiersTemplates(
            @Parameter(description = "Classifiers group templates type", required = true)
            @RequestParam ClassifierGroupTemplatesType classifierGroupTemplatesType) {
        log.info("Request classifiers group templates [{}]", classifierGroupTemplatesType);
        return classifiersFormTemplateProvider.getClassifiersTemplates(classifierGroupTemplatesType);
    }
}
