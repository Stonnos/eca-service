package com.ecaservice.core.filter.validation;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates page request according to specified filter template.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class PageRequestValidator implements ConstraintValidator<ValidPageRequest, PageRequestDto> {

    private static final String FILTERS_NODE_FORMAT = "filters[%d].name";

    private static final String INVALID_FILTER_REQUEST_FIELD_NAME_TEMPLATE = "{invalid.filter.request.field.name}";

    private static final String INVALID_SORT_FIELD_TEMPLATE = "{invalid.sort.field.name}";
    private static final String SORT_FIELD = "sortField[%d].sortField";

    private String filterTemplateName;

    private final FilterTemplateService filterTemplateService;

    @Override
    public void initialize(ValidPageRequest constraintAnnotation) {
        filterTemplateName = constraintAnnotation.filterTemplateName();
    }

    @Override
    public boolean isValid(PageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        boolean validFilters = validateFilterFields(pageRequestDto, context);
        boolean validSortField = validateSortFields(pageRequestDto, context);
        return validFilters && validSortField;
    }

    private boolean validateFilterFields(PageRequestDto pageRequestDto, ConstraintValidatorContext context) {
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

    private boolean validateSortFields(PageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(pageRequestDto.getSortFields())) {
            return true;
        } else {
            boolean valid = true;
            var sortFields = filterTemplateService.getSortFields(filterTemplateName);
            for (int i = 0; i < pageRequestDto.getSortFields().size(); i++) {
                var sortField = pageRequestDto.getSortFields().get(i);
                if (sortFields.stream().noneMatch(field -> field.equals(sortField.getSortField()))) {
                    valid = false;
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(INVALID_SORT_FIELD_TEMPLATE)
                            .addPropertyNode(String.format(SORT_FIELD, i))
                            .addConstraintViolation();
                }
            }
            return valid;
        }
    }
}
