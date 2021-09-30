package com.ecaservice.server.controller.web;

import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.report.model.ClassifiersConfigurationBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.server.controller.doc.ApiExamples;
import com.ecaservice.server.mapping.ClassifiersConfigurationMapper;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.service.classifiers.ClassifiersConfigurationService;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.server.config.audit.AuditCodes.SET_ACTIVE_CONFIGURATION;
import static com.ecaservice.server.util.ReportHelper.download;

/**
 * Implements API to manage experiment classifiers configurations.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API to manage experiment classifiers configurations")
@RestController
@RequestMapping("/experiment/classifiers-configurations")
@RequiredArgsConstructor
public class ClassifiersConfigurationController {

    private static final String CONFIGURATION_FILE_NAME_FORMAT = "%s configuration";

    private final ClassifiersConfigurationService classifiersConfigurationService;
    private final ClassifiersConfigurationMapper classifiersConfigurationMapper;

    /**
     * Finds classifiers configurations with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return classifiers configurations page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds classifiers configurations with specified options",
            summary = "Finds classifiers configurations with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = ApiExamples.SIMPLE_PAGE_REQUEST_JSON)
                    })
            })
    )
    @PostMapping(value = "/list")
    public PageDto<ClassifiersConfigurationDto> getClassifiersConfigurations(
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received classifiers configurations page request: {}", pageRequestDto);
        return classifiersConfigurationService.getClassifiersConfigurations(pageRequestDto);
    }

    /**
     * Gets classifiers configuration details.
     *
     * @param id - configuration id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets classifiers configuration details",
            summary = "Gets classifiers configuration details",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/details/{id}")
    public ClassifiersConfigurationDto getClassifiersConfigurationDetails(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @PathVariable Long id) {
        log.info("Gets classifier configuration details by id {}", id);
        return classifiersConfigurationService.getClassifiersConfigurationDetails(id);
    }

    /**
     * Saves new classifiers configuration.
     *
     * @param configurationDto - classifiers configuration
     * @return classifiers configuration dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Saves new classifiers configuration",
            summary = "Saves new classifiers configuration",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PostMapping(value = "/save")
    public ClassifiersConfigurationDto save(@Valid @RequestBody CreateClassifiersConfigurationDto configurationDto) {
        ClassifiersConfiguration classifiersConfiguration = classifiersConfigurationService.save(configurationDto);
        return classifiersConfigurationMapper.map(classifiersConfiguration);
    }

    /**
     * Updates classifiers configuration.
     *
     * @param configurationDto - classifiers configuration
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Updates classifiers configuration",
            summary = "Updates classifiers configuration",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PutMapping(value = "/update")
    public void update(@Valid @RequestBody UpdateClassifiersConfigurationDto configurationDto) {
        classifiersConfigurationService.update(configurationDto);
    }

    /**
     * Deletes classifiers configuration by specified id.
     *
     * @param id - configuration id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Deletes classifiers configuration",
            summary = "Deletes classifiers configuration",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @DeleteMapping(value = "/delete")
    public void delete(@Parameter(description = "Configuration id", example = "1", required = true)
                       @RequestParam long id) {
        classifiersConfigurationService.delete(id);
    }

    /**
     * Creates classifiers configuration copy.
     *
     * @param configurationDto - classifiers configuration
     * @return classifiers configuration dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Creates classifiers configuration copy",
            summary = "Creates classifiers configuration copy",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PostMapping(value = "/copy")
    public ClassifiersConfigurationDto copy(@Valid @RequestBody UpdateClassifiersConfigurationDto configurationDto) {
        ClassifiersConfiguration copy = classifiersConfigurationService.copy(configurationDto);
        return classifiersConfigurationMapper.map(copy);
    }

    /**
     * Sets classifiers configuration as active.
     *
     * @param id - configuration id
     */
    @Audit(SET_ACTIVE_CONFIGURATION)
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Sets classifiers configuration as active",
            summary = "Sets classifiers configuration as active",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PostMapping(value = "/set-active")
    public void setActive(@Parameter(description = "Configuration id", example = "1", required = true)
                          @RequestParam long id) {
        classifiersConfigurationService.setActive(id);
    }

    /**
     * Downloads classifiers configuration report in xlsx format.
     *
     * @param id                  - classifiers configuration id
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Downloads classifiers configuration report in xlsx format",
            summary = "Downloads classifiers configuration report in xlsx format",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/report/{id}")
    public void downloadReport(
            @Parameter(description = "Configuration id", example = "1", required = true) @PathVariable Long id,
            HttpServletResponse httpServletResponse) throws IOException {
        log.info("Downloads classifier configuration report by id {}", id);
        ClassifiersConfigurationBean classifiersConfigurationBean =
                classifiersConfigurationService.getClassifiersConfigurationReport(id);
        String fileName =
                String.format(CONFIGURATION_FILE_NAME_FORMAT, classifiersConfigurationBean.getConfigurationName());
        download(ReportType.CLASSIFIERS_CONFIGURATION, fileName, httpServletResponse, classifiersConfigurationBean);
    }
}
