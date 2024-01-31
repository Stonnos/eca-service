package com.ecaservice.core.filter.validation;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.web.dto.model.MultiSortPageRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidatorContext;

/**
 * Multi sort page request validator.
 *
 * @author Roman Batygin
 */
@Component
public class MultiSortPageRequestValidator extends PageRequestCustomValidator<MultiSortPageRequestDto> {

    private static final String INVALID_SORT_FIELD_TEMPLATE = "{invalid.sort.field.name}";
    private static final String SORT_FIELD = "sortField[%d].sortField";

    private final FilterTemplateService filterTemplateService;

    /**
     * Constructor with parameters.
     *
     * @param filterTemplateService - filter template service
     */
    public MultiSortPageRequestValidator(FilterTemplateService filterTemplateService) {
        super(MultiSortPageRequestDto.class);
        this.filterTemplateService = filterTemplateService;
    }

    @Override
    public boolean validate(MultiSortPageRequestDto pageRequestDto, ConstraintValidatorContext context,
                            String filterTemplateName) {
        if (!CollectionUtils.isEmpty(pageRequestDto.getSortFields())) {
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
        return true;
    }
}
