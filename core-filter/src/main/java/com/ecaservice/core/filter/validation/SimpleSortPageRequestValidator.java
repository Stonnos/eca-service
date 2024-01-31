package com.ecaservice.core.filter.validation;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

/**
 * Simple sort page request validator.
 *
 * @author Roman Batygin
 */
@Component
public class SimpleSortPageRequestValidator extends PageRequestCustomValidator<PageRequestDto> {

    private static final String INVALID_SORT_FIELD_TEMPLATE = "{invalid.sort.field.name}";
    private static final String SORT_FIELD = "sortField";

    private final FilterTemplateService filterTemplateService;

    /**
     * Constructor with parameters.
     *
     * @param filterTemplateService - filter template service
     */
    public SimpleSortPageRequestValidator(FilterTemplateService filterTemplateService) {
        super(PageRequestDto.class);
        this.filterTemplateService = filterTemplateService;
    }

    @Override
    public boolean validate(PageRequestDto pageRequestDto, ConstraintValidatorContext context,
                            String filterTemplateName) {
        if (StringUtils.isNotBlank(pageRequestDto.getSortField())) {
            var sortFields = filterTemplateService.getSortFields(filterTemplateName);
            if (!sortFields.contains(pageRequestDto.getSortField())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(INVALID_SORT_FIELD_TEMPLATE)
                        .addPropertyNode(SORT_FIELD)
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
