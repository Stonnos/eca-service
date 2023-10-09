package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.dto.ClassifierGroupTemplatesType;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.server.service.classifiers.ClassifierFormGroupTemplates.CLASSIFIERS_GROUP;
import static com.ecaservice.server.service.classifiers.ClassifierFormGroupTemplates.ENSEMBLE_CLASSIFIERS_GROUP;
import static com.ecaservice.server.util.ClassifierOptionsHelper.isEnsembleClassifierOptions;

/**
 * Classifiers templates provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersTemplateProvider {

    private final ClassifiersProperties classifiersProperties;
    private final FormTemplateProvider formTemplateProvider;

    /**
     * Gets classifiers form groups templates.
     *
     * @param classifierGroupTemplatesType - classifier group templates type
     * @return form groups templates list
     */
    public List<FormTemplateGroupDto> getClassifiersTemplates(
            ClassifierGroupTemplatesType classifierGroupTemplatesType) {
        log.info("Request classifiers group templates with type [{}]", classifierGroupTemplatesType);
        var groupTemplates = classifierGroupTemplatesType.getTemplateNames()
                .stream()
                .map(groupName -> {
                    var formTemplateGroupDto = formTemplateProvider.getFormGroupDto(groupName);
                    removeNotSupportedClassifierTemplates(formTemplateGroupDto);
                    return formTemplateGroupDto;
                })
                .collect(Collectors.toList());
        log.info("[{}] classifiers group form templates has been fetched for type [{}]", groupTemplates.size(),
                classifierGroupTemplatesType);
        return groupTemplates;
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

    private void removeNotSupportedClassifierTemplates(FormTemplateGroupDto formTemplateGroupDto) {
        var templates = formTemplateGroupDto.getTemplates();
        var supportedTemplates = templates
                .stream()
                .filter(formTemplateDto -> !classifiersProperties.getNotSupportedClassifierTemplates().contains(
                        formTemplateDto.getTemplateName()))
                .collect(Collectors.toList());
        formTemplateGroupDto.setTemplates(supportedTemplates);
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
