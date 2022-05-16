package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.web.dto.model.FieldDictionaryDto;
import com.ecaservice.web.dto.model.FieldDictionaryValueDto;
import com.ecaservice.web.dto.model.FieldType;
import com.ecaservice.web.dto.model.FormFieldDto;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.BeanUtil.invokeGetter;
import static com.ecaservice.server.util.ClassifierOptionsHelper.isEnsembleClassifierOptions;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.server.util.Utils.formatValue;

/**
 * Classifiers template service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersTemplateService {

    private static final String CLASSIFIERS_GROUP = "classifiers";
    private static final String ENSEMBLE_CLASSIFIERS_GROUP = "ensembleClassifiers";

    private final FormTemplateProvider formTemplateProvider;

    /**
     * Gets classifiers form templates.
     *
     * @return form templates list
     */
    public List<FormTemplateDto> getClassifiersTemplates() {
        log.info("Request classifiers templates");
        var classifierTemplates = formTemplateProvider.getTemplates(CLASSIFIERS_GROUP);
        log.info("[{}] classifiers form templates has been fetched", classifierTemplates.size());
        return classifierTemplates;
    }

    /**
     * Processes classifier input options json string to human readable format.
     *
     * @param classifierOptionsJson - classifier options json
     * @return classifier options list
     */
    @Deprecated
    public List<InputOptionDto> processInputOptions(String classifierOptionsJson) {
        log.debug("Starting to process classifier options json [{}]", classifierOptionsJson);
        var classifierOptions = parseOptions(classifierOptionsJson);
        var template = getTemplate(classifierOptions);
        var inputOptions = processInputOptions(template, classifierOptions);
        log.debug("Classifier options json has been processed with result: {}", inputOptions);
        return inputOptions;
    }

    /**
     * Gets classifier template by class.
     *
     * @param objectClass - classifier class
     * @return form template dto
     */
    public FormTemplateDto getClassifierTemplateByClass(String objectClass) {
        return getTemplate(CLASSIFIERS_GROUP, objectClass);
    }

    /**
     * Gets ensemble classifier template by class.
     *
     * @param objectClass - classifier class
     * @return form template dto
     */
    public FormTemplateDto getEnsembleClassifierTemplateByClass(String objectClass) {
        return getTemplate(ENSEMBLE_CLASSIFIERS_GROUP, objectClass);
    }

    private FormTemplateDto getTemplate(String groupName, String objectClass) {
        log.debug("Gets classifier template by group [{}] and class [{}]", groupName, objectClass);
        return formTemplateProvider.getTemplates(groupName)
                .stream()
                .filter(formTemplateDto -> formTemplateDto.getObjectClass().equals(objectClass))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find form template with group [%s] and class [%s]", groupName,
                                objectClass)));
    }

    private FormTemplateDto getTemplate(ClassifierOptions classifierOptions) {
        if (isEnsembleClassifierOptions(classifierOptions)) {
            return getEnsembleClassifierTemplateByClass(classifierOptions.getClass().getSimpleName());
        } else {
            return getClassifierTemplateByClass(classifierOptions.getClass().getSimpleName());
        }
    }

    private List<InputOptionDto> processInputOptions(FormTemplateDto template, ClassifierOptions classifierOptions) {
        return template.getFields()
                .stream()
                .map(formFieldDto -> {
                    var optionValue = getValue(classifierOptions, formFieldDto);
                    if (optionValue == null) {
                        log.debug("Got null value for field [{}] of type [{}]. Skipped...", formFieldDto.getFieldName(),
                                classifierOptions.getClass().getSimpleName());
                        return null;
                    }
                    InputOptionDto inputOptionDto = new InputOptionDto();
                    inputOptionDto.setOptionName(formFieldDto.getDescription());
                    inputOptionDto.setOptionValue(optionValue);
                    return inputOptionDto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getValue(ClassifierOptions classifierOptions, FormFieldDto formFieldDto) {
        var optionValue = invokeGetter(classifierOptions, formFieldDto.getFieldName());
        if (optionValue == null) {
            return null;
        }
        if (FieldType.REFERENCE.equals(formFieldDto.getFieldType()) && formFieldDto.getDictionary() != null) {
            log.debug("Gets field [{}] value from dictionary for code [{}]", formFieldDto.getFieldName(), optionValue);
            return getLabelFromDictionary(formFieldDto.getDictionary(), String.valueOf(optionValue));
        }
        return formatValue(optionValue);
    }

    private String getLabelFromDictionary(FieldDictionaryDto fieldDictionary, String code) {
        if (CollectionUtils.isEmpty(fieldDictionary.getValues())) {
            log.debug("Dictionary [{}] has empty values", fieldDictionary.getName());
            return null;
        }
        return fieldDictionary.getValues()
                .stream()
                .filter(fieldDictionaryValue -> fieldDictionaryValue.getValue().equals(code))
                .map(FieldDictionaryValueDto::getLabel)
                .findFirst()
                .orElse(null);
    }
}
