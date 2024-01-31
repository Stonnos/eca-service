package com.ecaservice.core.filter.validation;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.web.dto.model.FilterPageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Validates page request according to specified filter template.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class PageRequestValidator implements ConstraintValidator<ValidPageRequest, FilterPageRequestDto> {

    private static final String FILTERS_NODE_FORMAT = "filters[%d].name";

    private static final String INVALID_FILTER_REQUEST_FIELD_NAME_TEMPLATE = "{invalid.filter.request.field.name}";

    private String filterTemplateName;

    private final FilterTemplateService filterTemplateService;
    private final List<PageRequestCustomValidator> pageRequestCustomValidators;

    @Override
    public void initialize(ValidPageRequest constraintAnnotation) {
        filterTemplateName = constraintAnnotation.filterTemplateName();
    }

    @Override
    public boolean isValid(FilterPageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        boolean validFilters = validateFilterFields(pageRequestDto, context);
        boolean validSortField = validateCustomFields(pageRequestDto, context);
        return validFilters && validSortField;
    }

    private boolean validateFilterFields(FilterPageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        boolean valid = true;
        if (!CollectionUtils.isEmpty(pageRequestDto.getFilters())) {
            var filterFields = filterTemplateService.getFilterFields(filterTemplateName);
            for (int i = 0; i < pageRequestDto.getFilters().size(); i++) {
                var filterRequestDto = pageRequestDto.getFilters().get(i);
                if (filterFields.stream().noneMatch(
                        filterFieldDto -> filterFieldDto.getFieldName().equals(filterRequestDto.getName()))) {
                    valid = false;
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(INVALID_FILTER_REQUEST_FIELD_NAME_TEMPLATE)
                            .addPropertyNode(String.format(FILTERS_NODE_FORMAT, i))
                            .addConstraintViolation();
                }
            }
        }
        return valid;
    }

    @SuppressWarnings("unchecked")
    private boolean validateCustomFields(FilterPageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        var customValidator = pageRequestCustomValidators.stream()
                .filter(validator -> validator.canHandle(pageRequestDto))
                .findFirst()
                .orElse(null);
        if (customValidator == null) {
            return true;
        } else {
            return customValidator.validate(pageRequestDto, context, filterTemplateName);
        }
    }
}
