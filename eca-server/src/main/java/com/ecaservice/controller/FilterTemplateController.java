package com.ecaservice.controller;

import com.ecaservice.mapping.filters.FilterFieldMapper;
import com.ecaservice.model.entity.FilterTemplate;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.repository.FilterTemplateRepository;
import com.ecaservice.web.dto.model.FilterFieldDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final FilterFieldMapper filterFieldMapper;
    private final FilterTemplateRepository filterTemplateRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterFieldMapper        - filter field mapper bean
     * @param filterTemplateRepository - filter template repository bean
     */
    @Inject
    public FilterTemplateController(FilterFieldMapper filterFieldMapper,
                                    FilterTemplateRepository filterTemplateRepository) {
        this.filterFieldMapper = filterFieldMapper;
        this.filterTemplateRepository = filterTemplateRepository;
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
        return getFilterFields(FilterTemplateType.EXPERIMENT);
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
        return getFilterFields(FilterTemplateType.EVALUATION_LOG);
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
        return getFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST);
    }

    private ResponseEntity<List<FilterFieldDto>> getFilterFields(FilterTemplateType templateType) {
        log.info("Received request for filter fields for template type [{}]", templateType);
        FilterTemplate filterTemplate = filterTemplateRepository.findFirstByTemplateType(templateType);
        if (filterTemplate == null) {
            log.error("Can't find filter template with type [{}]", templateType);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(filterFieldMapper.map(filterTemplate.getFields()));
    }
}
