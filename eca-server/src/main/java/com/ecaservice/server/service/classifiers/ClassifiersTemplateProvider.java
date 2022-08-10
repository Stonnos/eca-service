package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.web.dto.model.FormTemplateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return formTemplateProvider.getTemplates(groupName)
                .stream()
                .filter(formTemplateDto -> formTemplateDto.getObjectClass().equals(objectClass))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find form template with group [%s] and class [%s]", groupName,
                                objectClass)));
    }
}
