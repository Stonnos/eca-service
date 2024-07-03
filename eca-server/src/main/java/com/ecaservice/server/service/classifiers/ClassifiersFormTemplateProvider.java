package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.template.processor.service.ClassifiersTemplateProvider;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.server.dto.ClassifierGroupTemplatesType;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classifiers templates provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersFormTemplateProvider {

    private final ClassifiersTemplateProvider classifiersTemplateProvider;
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
                .map(formTemplateProvider::getFormGroupDto)
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
        return classifiersTemplateProvider.getClassifierTemplateByClass(objectClass);
    }

    /**
     * Gets form template for classifier options.
     *
     * @param classifierOptions - classifier options
     * @return form template dto
     */
    public FormTemplateDto getTemplate(ClassifierOptions classifierOptions) {
        return classifiersTemplateProvider.getTemplate(classifierOptions);
    }
}
