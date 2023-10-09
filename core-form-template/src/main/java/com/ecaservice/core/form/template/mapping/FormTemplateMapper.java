package com.ecaservice.core.form.template.mapping;

import com.ecaservice.core.form.template.entity.FieldDictionary;
import com.ecaservice.core.form.template.entity.FieldDictionaryValue;
import com.ecaservice.core.form.template.entity.FormFieldEntity;
import com.ecaservice.core.form.template.entity.FormTemplateEntity;
import com.ecaservice.core.form.template.entity.FormTemplateGroupEntity;
import com.ecaservice.web.dto.model.FieldDictionaryDto;
import com.ecaservice.web.dto.model.FieldDictionaryValueDto;
import com.ecaservice.web.dto.model.FormFieldDto;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Form field mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface FormTemplateMapper {

    /**
     * Maps form template entity to dto model
     *
     * @param templateEntity - form template entity
     * @return form template dto
     */
    FormTemplateDto map(FormTemplateEntity templateEntity);

    /**
     * Maps form templates group entity to dto model.
     *
     * @param formTemplateGroupEntity - form templates group entity
     * @return form templates group dto
     */
    FormTemplateGroupDto map(FormTemplateGroupEntity formTemplateGroupEntity);

    /**
     * Maps form templates entities to dto models list
     *
     * @param templates - form templates entities
     * @return form templates dto list
     */
    List<FormTemplateDto> mapTemplates(List<FormTemplateEntity> templates);

    /**
     * Maps field entity to its dto model.
     *
     * @param formFieldEntity - form field entity
     * @return form field dto model
     */
    FormFieldDto map(FormFieldEntity formFieldEntity);

    /**
     * Maps form field entities to its dto models.
     *
     * @param formFields - form field entities
     * @return form fields dto models list
     */
    List<FormFieldDto> map(List<FormFieldEntity> formFields);

    /**
     * Maps field dictionary entity to its dto model.
     *
     * @param fieldDictionary - field dictionary entity
     * @return field dictionary dto
     */
    FieldDictionaryDto map(FieldDictionary fieldDictionary);

    /**
     * Maps field dictionary value entity to its dto model.
     *
     * @param fieldDictionaryValue - field dictionary value entity
     * @return field dictionary value dto model
     */
    FieldDictionaryValueDto map(FieldDictionaryValue fieldDictionaryValue);

    /**
     * Maps field dictionary values entities to its dto models.
     *
     * @param fieldDictionaryValues - field dictionary values entities
     * @return field dictionary values dto models list
     */
    List<FieldDictionaryValueDto> mapDictionaryValues(List<FieldDictionaryValue> fieldDictionaryValues);
}
