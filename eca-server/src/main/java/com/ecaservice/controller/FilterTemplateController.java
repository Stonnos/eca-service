package com.ecaservice.controller;

import com.ecaservice.mapping.filters.FilterFieldMapper;
import com.ecaservice.model.entity.FilterTemplate;
import com.ecaservice.repository.FilterTemplateRepository;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterTemplateType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RestController(value = "/filters")
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
     * Gets filter fields for specified template type.
     *
     * @param templateType - filter template type
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets filter fields for specified template type",
            notes = "Gets filter fields for specified template type"
    )
    @GetMapping(value = "/template")
    public ResponseEntity<List<FilterFieldDto>> getFilterFields(@RequestParam FilterTemplateType templateType) {
        log.info("Received request for filter fields for template type [{}]", templateType);
        FilterTemplate filterTemplate = filterTemplateRepository.findFirstByTemplateType(templateType);
        if (filterTemplate == null) {
            log.error("Can't find filter template with type [{}]", templateType);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(filterFieldMapper.map(filterTemplate.getFields()));
    }
}
