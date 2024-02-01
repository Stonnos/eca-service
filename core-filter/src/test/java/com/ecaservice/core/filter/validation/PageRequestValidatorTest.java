package com.ecaservice.core.filter.validation;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link  PageRequestValidator} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
class PageRequestValidatorTest {

    private static final String FILTER_TEMPLATE_NAME = "templateName";
    private static final String FIELD_1 = "field1";
    private static final String FIELD_2 = "field2";
    private static final int PAGE = 0;
    private static final int PAGE_SIZE = 10;
    private static final String VALUE = "value";

    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext customizableContext;

    @MockBean
    private FilterTemplateService filterTemplateService;

    private PageRequestValidator pageRequestValidator;

    @BeforeEach
    void init() {
        pageRequestValidator = new PageRequestValidator(filterTemplateService);
        var validPageRequest = mock(ValidPageRequest.class);
        when(validPageRequest.filterTemplateName()).thenReturn(FILTER_TEMPLATE_NAME);
        pageRequestValidator.initialize(validPageRequest);
        mockFilterFields();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(customizableContext);
    }

    @Test
    void testValidFilterFields() {
        var pageRequestDto =
                new PageRequestDto(PAGE, PAGE_SIZE, Collections.singletonList(new SortFieldRequestDto(FIELD_1, false)),
                        StringUtils.EMPTY, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(FIELD_1, Collections.singletonList(VALUE), MatchMode.LIKE));
        boolean valid = pageRequestValidator.isValid(pageRequestDto, context);
        assertThat(valid).isTrue();
    }

    @Test
    void testInvalidFilterFields() {
        var pageRequestDto =
                new PageRequestDto(PAGE, PAGE_SIZE, Collections.singletonList(new SortFieldRequestDto(FIELD_1, false)),
                        StringUtils.EMPTY, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto("field3", Collections.singletonList(VALUE), MatchMode.LIKE));
        boolean valid = pageRequestValidator.isValid(pageRequestDto, context);
        assertThat(valid).isFalse();
    }

    @Test
    void testInvalidSortFields() {
        var pageRequestDto =
                new PageRequestDto(PAGE, PAGE_SIZE, Collections.singletonList(new SortFieldRequestDto("field3", false)),
                        StringUtils.EMPTY, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(FIELD_1, Collections.singletonList(VALUE), MatchMode.LIKE));
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(customizableContext);
        boolean valid = pageRequestValidator.isValid(pageRequestDto, context);
        assertThat(valid).isFalse();
    }

    private void mockFilterFields() {
        FilterFieldDto first = new FilterFieldDto();
        first.setFieldName(FIELD_1);
        FilterFieldDto second = new FilterFieldDto();
        second.setFieldName(FIELD_2);
        when(filterTemplateService.getFilterFields(FILTER_TEMPLATE_NAME)).thenReturn(List.of(first, second));
        when(filterTemplateService.getSortFields(FILTER_TEMPLATE_NAME)).thenReturn(List.of(FIELD_1, FIELD_2));
    }
}
