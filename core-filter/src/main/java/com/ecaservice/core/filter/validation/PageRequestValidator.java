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
    private static final String INVALID_FILTER_REQUEST_FIELD_NAME_TEMPLATE = "{invalid.filter.request.field.name}";

    private String filterTemplateName;

    private final FilterService filterService;

    @Override
    public void initialize(ValidPageRequest constraintAnnotation) {
        filterTemplateName = constraintAnnotation.filterTemplateName();
    }

    @Override
    public boolean isValid(PageRequestDto pageRequestDto, ConstraintValidatorContext context) {
        boolean valid = true;
        if (!CollectionUtils.isEmpty(pageRequestDto.getFilters())) {
            var filterFields = filterService.getFilterFields(filterTemplateName);
            for (FilterRequestDto filterRequestDto : pageRequestDto.getFilters()) {
                if (filterFields.stream().noneMatch(
                        filterFieldDto -> filterFieldDto.getFieldName().equals(filterRequestDto.getName()))) {
                    valid = false;
                    buildConstraintViolationWithTemplate(context, INVALID_FILTER_REQUEST_FIELD_NAME_TEMPLATE,
                            filterRequestDto.getName());
                }
            }
        }
        return valid;
    }

    private void buildConstraintViolationWithTemplate(ConstraintValidatorContext context,
                                                      String template,
                                                      String parameter) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(template)
                .addPropertyNode(String.format(FILTERS_NODE_FORMAT, parameter))
                .addConstraintViolation();
    }
}
