package com.ecaservice.controller;

import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.web.dto.model.FilterFieldDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
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
public class FilterTemplateController {

    private final FilterService filterService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService - filter service bean
     */
    @Inject
    public FilterTemplateController(FilterService filterService) {
        this.filterService = filterService;
    }

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
    public ResponseEntity<List<FilterFieldDto>> getExperimentFilter() {
        return ResponseEntity.ok(filterService.getFilterFields(FilterTemplateType.EXPERIMENT));
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
    public ResponseEntity<List<FilterFieldDto>> getEvaluationLogFilter() {
        return ResponseEntity.ok(filterService.getFilterFields(FilterTemplateType.EVALUATION_LOG));
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
    public ResponseEntity<List<FilterFieldDto>> getClassifierOptionsRequestFilter() {
        return ResponseEntity.ok(filterService.getFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST));
    }

    /**
     * Handles bad request error.
     *
     * @param ex - illegal argument exception
     * @return response entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleBadRequest(IllegalArgumentException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
