package com.ecaservice.core.filter.validation;

import com.ecaservice.web.dto.model.FilterPageRequestDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidatorContext;

/**
 * PageRequest custom validator.
 *
 * @param <T> - page request generic type
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PageRequestCustomValidator<T extends FilterPageRequestDto> {

    @Getter
    private final Class<T> type;

    /**
     * Returns {@code true} if page request can be handled.
     *
     * @param pageRequestDto - page request dto
     * @return {@code true} if page request can be handled
     */
    public boolean canHandle(FilterPageRequestDto pageRequestDto) {
        return pageRequestDto != null && type.isAssignableFrom(pageRequestDto.getClass());
    }

    /**
     * Validates page request.
     *
     * @param filterPageRequestDto - page request dto
     * @param context              - validator context
     * @param filterTemplateName   - filer template name
     * @return {@code true} if page request is valid
     */
    public abstract boolean validate(T filterPageRequestDto, ConstraintValidatorContext context,
                                     String filterTemplateName);
}
