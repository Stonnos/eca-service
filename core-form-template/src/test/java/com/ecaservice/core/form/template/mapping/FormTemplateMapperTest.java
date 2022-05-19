package com.ecaservice.core.form.template.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.core.form.template.TestHelperUtils.createFieldDictionary;
import static com.ecaservice.core.form.template.TestHelperUtils.createFieldDictionaryValue;
import static com.ecaservice.core.form.template.TestHelperUtils.createFormFieldEntity;
import static com.ecaservice.core.form.template.TestHelperUtils.createFormTemplateEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FormTemplateMapper} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(FormTemplateMapperImpl.class)
class FormTemplateMapperTest {

    private static final int FIELD_ORDER = 0;
    private static final String FIELD_NAME = "field";

    @Inject
    private FormTemplateMapper formTemplateMapper;

    @Test
    void testMapFieldDictionary() {
        var dictionaryEntity = createFieldDictionary();
        var dictionaryDto = formTemplateMapper.map(dictionaryEntity);
        assertThat(dictionaryDto).isNotNull();
        assertThat(dictionaryDto.getName()).isEqualTo(dictionaryEntity.getName());
        assertThat(dictionaryDto.getValues()).hasSameSizeAs(dictionaryEntity.getValues());
    }

    @Test
    void testMapFieldDictionaryValue() {
        var fieldDictionaryValue = createFieldDictionaryValue();
        var fieldDictionaryValueDto = formTemplateMapper.map(fieldDictionaryValue);
        assertThat(fieldDictionaryValueDto).isNotNull();
        assertThat(fieldDictionaryValueDto.getValue()).isEqualTo(fieldDictionaryValue.getValue());
        assertThat(fieldDictionaryValueDto.getLabel()).isEqualTo(fieldDictionaryValue.getLabel());
    }

    @Test
    void testMapFormField() {
        var formFieldEntity = createFormFieldEntity(FIELD_NAME, FIELD_ORDER);
        var formFieldDto = formTemplateMapper.map(formFieldEntity);
        assertThat(formFieldDto).isNotNull();
        assertThat(formFieldDto.getFieldName()).isEqualTo(formFieldEntity.getFieldName());
        assertThat(formFieldDto.getDescription()).isEqualTo(formFieldEntity.getDescription());
        assertThat(formFieldDto.getFieldOrder()).isEqualTo(formFieldEntity.getFieldOrder());
        assertThat(formFieldDto.getFieldType()).isEqualTo(formFieldEntity.getFieldType());
        assertThat(formFieldDto.getMaxLength()).isEqualTo(formFieldEntity.getMaxLength());
        assertThat(formFieldDto.getPattern()).isEqualTo(formFieldEntity.getPattern());
        assertThat(formFieldDto.getMinValue()).isEqualTo(formFieldEntity.getMinValue());
        assertThat(formFieldDto.isMinInclusive()).isEqualTo(formFieldEntity.isMinInclusive());
        assertThat(formFieldDto.isMaxInclusive()).isEqualTo(formFieldEntity.isMaxInclusive());
        assertThat(formFieldDto.getMaxValue()).isEqualTo(formFieldEntity.getMaxValue());
        assertThat(formFieldDto.getDefaultValue()).isEqualTo(formFieldEntity.getDefaultValue());
        assertThat(formFieldDto.isReadOnly()).isEqualTo(formFieldEntity.isReadOnly());
    }

    @Test
    void testMapFormTemplate() {
        var formTemplateEntity = createFormTemplateEntity();
        var formTemplateDto = formTemplateMapper.map(formTemplateEntity);
        assertThat(formTemplateDto).isNotNull();
        assertThat(formTemplateDto.getTemplateName()).isEqualTo(formTemplateEntity.getTemplateName());
        assertThat(formTemplateDto.getTemplateTitle()).isEqualTo(formTemplateEntity.getTemplateTitle());
        assertThat(formTemplateDto.getObjectClass()).isEqualTo(formTemplateDto.getObjectClass());
        assertThat(formTemplateDto.getObjectType()).isEqualTo(formTemplateDto.getObjectType());
        assertThat(formTemplateDto.getFields()).hasSameSizeAs(formTemplateEntity.getFields());
    }
}
