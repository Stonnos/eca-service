package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;

/**
 * Service for processing classifier info.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsInfoProcessor {

    private final ClassifierOptionsProcessor classifierOptionsProcessor;

    /**
     * Processes classifier input options json string to human readable format.
     *
     * @param classifierOptions - classifier options
     * @return classifier options list
     */
    public List<InputOptionDto> processInputOptions(ClassifierOptions classifierOptions) {
        return classifierOptionsProcessor.processInputOptions(classifierOptions);
    }

    /**
     * Processes classifier input options json string to classifier info.
     *
     * @param classifierOptionsJson - classifier options json
     * @return classifier info dto
     */
    public ClassifierInfoDto processClassifierInfo(String classifierOptionsJson) {
        log.debug("Starting to process classifier info [{}]", classifierOptionsJson);
        var classifierOptions = parseOptions(classifierOptionsJson);
        return classifierOptionsProcessor.processClassifierOptions(classifierOptions);
    }

    /**
     * Processes template title.
     *
     * @param formTemplateDto   - form template dto
     * @param classifierOptions - classifier options
     * @return template title
     */
    public String processTemplateTitle(FormTemplateDto formTemplateDto, ClassifierOptions classifierOptions) {
        return classifierOptionsProcessor.processTemplateTitle(formTemplateDto, classifierOptions);
    }
}
