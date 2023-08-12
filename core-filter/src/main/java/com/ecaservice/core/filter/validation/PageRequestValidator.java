package com.ecaservice.core.filter.validation;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates page request according to specified filter template.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class PageRequestValidator implements ConstraintValidator<ValidPageRequest, PageRequestDto> {

    private static final String FILTERS_NODE_FORMAT = "filters[%s]";

    private static final String SORT_FIELD_NODE_FORMAT = "sortField[%s]";
    private static final String INVALID_FILTER_REQUEST_FIELD_NAME_TEMPLATE = "{invalid.filter.request.field.name}";

    private static final String INVALID_SORT_FIELD_TEMPLATE = "{invalid.sort.field.name}";

    private String filterTemplateName;

    private final FilterService filterService;

    @Override
    public void initialize(ValidPageRequest constraintAnnotation) {
        filterTemplateName = constraintAnnotation.filterTemplateName();
    }

    @Override
    public boolean isValid(PageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        boolean validFilters = validateFilterFields(pageRequestDto, context);
        boolean validSortField = validateSortField(pageRequestDto, context);
        return validFilters && validSortField;
    }

    private boolean validateFilterFields(PageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        boolean valid = true;
        if (!CollectionUtils.isEmpty(pageRequestDto.getFilters())) {
            var filterFields = filterService.getFilterFields(filterTemplateName);
            for (FilterRequestDto filterRequestDto : pageRequestDto.getFilters()) {
                if (filterFields.stream().noneMatch(
                        filterFieldDto -> filterFieldDto.getFieldName().equals(filterRequestDto.getName()))) {
                    valid = false;
                    buildConstraintViolationWithTemplate(context, INVALID_FILTER_REQUEST_FIELD_NAME_TEMPLATE,
                            String.format(FILTERS_NODE_FORMAT, filterRequestDto.getName()));
                }
            }
        }
        return valid;
    }

    private boolean validateSortField(PageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        var sortFields = filterService.getSortFields(filterTemplateName);
        if (!sortFields.contains(pageRequestDto.getSortField())) {
            buildConstraintViolationWithTemplate(context, INVALID_SORT_FIELD_TEMPLATE,
                    String.format(SORT_FIELD_NODE_FORMAT, pageRequestDto.getSortField()));
            return false;
        }
        return true;
    }

    private void buildConstraintViolationWithTemplate(ConstraintValidatorContext context,
                                                      String template,
                                                      String node) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(template)
                .addPropertyNode(node)
                .addConstraintViolation();
    }
}
