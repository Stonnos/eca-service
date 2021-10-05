package com.ecaservice.server.controller.web;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.service.filter.dictionary.FilterDictionaries;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;

/**
 * Filter templates controller for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "Filter templates controller for web application")
@Slf4j
@RestController
@RequestMapping("/filter-templates")
@RequiredArgsConstructor
public class FilterTemplateController {

    private final FilterService filterService;

    /**
     * Gets experiment filter fields.
     *
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets experiment filter fields",
            summary = "Gets experiment filter fields",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/experiment")
    public List<FilterFieldDto> getExperimentFilter() {
        return filterService.getFilterFields(FilterTemplateType.EXPERIMENT.name());
    }

    /**
     * Gets evaluation log filter fields.
     *
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets evaluation log filter fields",
            summary = "Gets evaluation log filter fields",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/evaluation")
    public List<FilterFieldDto> getEvaluationLogFilter() {
        return filterService.getFilterFields(FilterTemplateType.EVALUATION_LOG.name());
    }

    /**
     * Gets classifier options request filter fields
     *
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets classifier options request filter fields",
            summary = "Gets classifier options request filter fields",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/classifier-options-request")
    public List<FilterFieldDto> getClassifierOptionsRequestFilter() {
        return filterService.getFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST.name());
    }

    /**
     * Gets experiment types filter dictionary.
     *
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets experiment types filter dictionary",
            summary = "Gets experiment types filter dictionary",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/experiment-types")
    public FilterDictionaryDto getExperimentTypeDictionary() {
        return filterService.getFilterDictionary(FilterDictionaries.EXPERIMENT_TYPE);
    }

    /**
     * Gets evaluation method filter dictionary.
     *
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets evaluation method filter dictionary",
            summary = "Gets evaluation method filter dictionary",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/evaluation-methods")
    public FilterDictionaryDto getEvaluationMethodDictionary() {
        return filterService.getFilterDictionary(FilterDictionaries.EVALUATION_METHOD);
    }
}