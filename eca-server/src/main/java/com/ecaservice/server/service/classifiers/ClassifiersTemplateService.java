package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.BeanUtil.invokeGetter;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.google.common.collect.Lists.newArrayList;

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
    public List<InputOptionDto> processInputOptions(String classifierOptionsJson) {
        log.debug("Starting to process classifier options json [{}]", classifierOptionsJson);
        var classifierOptions = parseOptions(classifierOptionsJson);
        var inputOptions = processInputOptions(classifierOptions);
        log.debug("Classifier options json has been processed with result: {}", inputOptions);
        return inputOptions;
    }

    private List<InputOptionDto> processInputOptions(ClassifierOptions classifierOptions) {
        //FIXME how to get template name
        var template = formTemplateProvider.getTemplate("x");
        return template.getFields()
                .stream()
                .map(formFieldDto -> {
                    var optionValue = invokeGetter(classifierOptions, formFieldDto.getFieldName());
                    if (optionValue == null) {
                        log.debug("Got null value for field [{}] of type [{}]", formFieldDto.getFieldName(),
                                classifierOptions.getClass().getSimpleName());
                        return null;
                    }
                    InputOptionDto inputOptionDto = new InputOptionDto();
                    inputOptionDto.setOptionName(formFieldDto.getDescription());
                    inputOptionDto.setOptionValue(String.valueOf(optionValue));
                    return inputOptionDto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
