package com.ecaservice.core.form.template;

import com.ecaservice.core.form.template.entity.FieldDictionary;
import com.ecaservice.core.form.template.entity.FieldDictionaryValue;
import com.ecaservice.core.form.template.entity.FormFieldEntity;
import com.ecaservice.core.form.template.entity.FormTemplateEntity;
import com.ecaservice.web.dto.model.FieldType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Collections;
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
    private static final int FORM_TEMPLATE_FIELDS = 5;
    private static final String DESCRIPTION = "description";
    private static final String PATTERN = "pattern";
    private static final String DEFAULT_VALUE = "default";
    private static final String TEMPLATE_TITLE = "Template title";
    private static final String TEMPLATE_NAME = "templateName";
    private static final String DICTIONARY_NAME = "dictionaryName";

    /**
     * Creates field dictionary.
     *
     * @return field dictionary
     */
    public static FieldDictionary createFieldDictionary() {
        var fieldDictionary = new FieldDictionary();
        fieldDictionary.setName(DICTIONARY_NAME);
        fieldDictionary.setValues(Collections.singletonList(createFieldDictionaryValue()));
        return fieldDictionary;
    }

    /**
     * Creates field dictionary value.
     *
     * @return field dictionary value
     */
    public static FieldDictionaryValue createFieldDictionaryValue() {
        var fieldDictionaryValue = new FieldDictionaryValue();
        fieldDictionaryValue.setLabel(LABEL);
        fieldDictionaryValue.setValue(VALUE);
        return fieldDictionaryValue;
    }

    /**
     * Creates form field.
     *
     * @param fieldName  - field name
     * @param fieldOrder - field order
     * @return form field
     */
    public static FormFieldEntity createFormFieldEntity(String fieldName, int fieldOrder) {
        var formFieldEntity = new FormFieldEntity();
        formFieldEntity.setFieldName(fieldName);
        formFieldEntity.setDescription(DESCRIPTION);
        formFieldEntity.setFieldOrder(fieldOrder);
        formFieldEntity.setFieldType(FieldType.TEXT);
        formFieldEntity.setMaxLength(255);
        formFieldEntity.setPattern(PATTERN);
        formFieldEntity.setDefaultValue(DEFAULT_VALUE);
        formFieldEntity.setMinValue(BigDecimal.ZERO);
        formFieldEntity.setMaxValue(BigDecimal.ONE);
        return formFieldEntity;
    }

    /**
     * Creates form template.
     *
     * @return form template
     */
    public static FormTemplateEntity createFormTemplateEntity() {
        var formTemplateEntity = new FormTemplateEntity();
        formTemplateEntity.setTemplateName(TEMPLATE_NAME);
        formTemplateEntity.setTemplateTitle(TEMPLATE_TITLE);
        formTemplateEntity.setFields(IntStream.range(0, FORM_TEMPLATE_FIELDS)
                .mapToObj(i -> createFormFieldEntity(String.valueOf(i), i))
                .collect(Collectors.toList())
        );
        return formTemplateEntity;
    }
}
