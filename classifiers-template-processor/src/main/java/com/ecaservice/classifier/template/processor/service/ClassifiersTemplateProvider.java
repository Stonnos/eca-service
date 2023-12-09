package com.ecaservice.classifier.template.processor.service;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.web.dto.model.FormTemplateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.classifier.template.processor.service.ClassifierFormGroupTemplates.CLASSIFIERS_GROUP;
import static com.ecaservice.classifier.template.processor.service.ClassifierFormGroupTemplates.ENSEMBLE_CLASSIFIERS_GROUP;
import static com.ecaservice.classifier.template.processor.util.Utils.isEnsembleClassifierOptions;

/**
 * Classifiers templates provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersTemplateProvider {

    private final FormTemplateProvider formTemplateProvider;

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

    /**
     * Gets form template for classifier options.
     *
     * @param classifierOptions - classifier options
     * @return form template dto
     */
    public FormTemplateDto getTemplate(ClassifierOptions classifierOptions) {
        if (isEnsembleClassifierOptions(classifierOptions)) {
            return getEnsembleClassifierTemplateByClass(classifierOptions.getClass().getSimpleName());
        } else {
            return getClassifierTemplateByClass(classifierOptions.getClass().getSimpleName());
        }
    }

    private FormTemplateDto getTemplate(String groupName, String objectClass) {
        log.debug("Gets classifier template by group [{}] and class [{}]", groupName, objectClass);
        return formTemplateProvider.getFormGroupDto(groupName)
                .getTemplates()
                .stream()
                .filter(formTemplateDto -> formTemplateDto.getObjectClass().equals(objectClass))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find form template with group [%s] and class [%s]", groupName,
                                objectClass)));
    }
}
