package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.server.model.entity.ClassifierInfo;
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
     * @param classifierInfo - classifier options
     * @return classifier info
     */
    public ClassifierInfoDto processClassifierInfo(ClassifierInfo classifierInfo) {
        log.debug("Starting to process classifier info [{}]", classifierInfo.getClassifierName());
        var classifierOptions = parseOptions(classifierInfo.getClassifierOptions());
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
