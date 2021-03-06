package com.ecaservice.controller.web;

import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.service.filter.dictionary.FilterDictionaries;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Filter templates controller for web application.
 *
 * @author Roman Batygin
 */
@Api(tags = "Filter templates controller for web application")
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
    @ApiOperation(
            value = "Gets experiment filter fields",
            notes = "Gets experiment filter fields"
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
    @ApiOperation(
            value = "Gets evaluation log filter fields",
            notes = "Gets evaluation log filter fields"
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
    @ApiOperation(
            value = "Gets classifier options request filter fields",
            notes = "Gets classifier options request filter fields"
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
    @ApiOperation(
            value = "Gets experiment types filter dictionary",
            notes = "Gets experiment types filter dictionary"
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
    @ApiOperation(
            value = "Gets evaluation method filter dictionary",
            notes = "Gets evaluation method filter dictionary"
    )
    @GetMapping(value = "/evaluation-methods")
    public FilterDictionaryDto getEvaluationMethodDictionary() {
        return filterService.getFilterDictionary(FilterDictionaries.EVALUATION_METHOD);
    }
}
