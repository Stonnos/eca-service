package com.ecaservice.core.filter;

import com.ecaservice.core.filter.entity.FilterDictionaryValue;
import com.ecaservice.core.filter.entity.FilterField;
import com.ecaservice.core.filter.entity.FilterTemplate;
import com.ecaservice.core.filter.entity.GlobalFilterField;
import com.ecaservice.core.filter.entity.GlobalFilterTemplate;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Test data helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String LABEL = "label";
    private static final String VALUE = "value";
    private static final String FILTER_NAME = "name";
    private static final String FILTER_DESCRIPTION = "description";
    private static final int FILTER_TEMPLATE_FIELDS = 5;
    private static final String DESCRIPTION = "description";

    /**
     * Creates filter dictionary value.
     *
     * @return filter dictionary value
     */
    public static FilterDictionaryValue createFilterDictionaryValue() {
        FilterDictionaryValue filterDictionaryValue = new FilterDictionaryValue();
        filterDictionaryValue.setLabel(LABEL);
        filterDictionaryValue.setValue(VALUE);
        return filterDictionaryValue;
    }

    /**
     * Creates filter field.
     *
     * @param name  - field name
     * @param order - field order
     * @return filter field
     */
    public static FilterField createFilterField(String name, int order) {
        FilterField filterField = new FilterField();
        filterField.setFieldName(name);
        filterField.setDescription(DESCRIPTION);
        filterField.setFieldOrder(order);
        filterField.setFilterFieldType(FilterFieldType.TEXT);
        filterField.setMatchMode(MatchMode.LIKE);
        return filterField;
    }

    /**
     * Creates filter template.
     *
     * @param filterTemplateType - filter template type
     * @return filter template
     */
    public static FilterTemplate createFilterTemplate(String filterTemplateType) {
        FilterTemplate filterTemplate = new FilterTemplate();
        filterTemplate.setTemplateName(FILTER_NAME);
        filterTemplate.setTemplateType(filterTemplateType);
        filterTemplate.setCreated(LocalDateTime.now());
        filterTemplate.setFields(IntStream.range(0, FILTER_TEMPLATE_FIELDS).mapToObj(
                i -> createFilterField(String.valueOf(i), i)).collect(Collectors.toList()));
        return filterTemplate;
    }

    /**
     * Creates global filter template.
     *
     * @param filterTemplateType - filter template type
     * @return global filter template
     */
    public static GlobalFilterTemplate createGlobalFilterTemplate(String filterTemplateType) {
        GlobalFilterTemplate filterTemplate = new GlobalFilterTemplate();
        filterTemplate.setFilterName(FILTER_NAME);
        filterTemplate.setTemplateType(filterTemplateType);
        filterTemplate.setFields(IntStream.range(0, FILTER_TEMPLATE_FIELDS).mapToObj(
                i -> createGlobalFilterField(String.valueOf(i))).collect(Collectors.toList()));
        return filterTemplate;
    }

    /**
     * Creates global filter field.
     *
     * @param name - field name
     * @return global filter field
     */
    public static GlobalFilterField createGlobalFilterField(String name) {
        GlobalFilterField filterField = new GlobalFilterField();
        filterField.setFieldName(name);
        return filterField;
    }
}
