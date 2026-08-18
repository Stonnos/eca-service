package com.ecaservice.server.service.audit.custom;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.core.audit.service.AuditContextParamsEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.server.config.audit.AuditContextCustomParams.CLASSIFIER_INPUT_OPTIONS_DETAILS;
import static com.ecaservice.server.util.ClassifierOptionsHelper.getCommaSeparatedOptions;
import static com.ecaservice.server.util.Utils.getTypedValue;

/**
 * Add classifier options audit context parameters evaluator.
 *
 * @author Roman Batygin
 */
@Component(AddClassifierOptionsAuditContextParamsEvaluator.ADD_CLASSIFIER_OPTIONS_AUDIT_CONTEXT_PARAMS_EVALUATOR)
@RequiredArgsConstructor
public class AddClassifierOptionsAuditContextParamsEvaluator implements AuditContextParamsEvaluator {

    public static final String ADD_CLASSIFIER_OPTIONS_AUDIT_CONTEXT_PARAMS_EVALUATOR =
            "AddClassifierOptionsAuditContextParamsEvaluator";
    private static final String CLASSIFIER_OPTIONS_PARAM = "classifierOptions";

    private final ClassifierOptionsProcessor classifierOptionsProcessor;

    @Override
    public Map<String, Object> evaluate(Map<String, Object> methodParams, Object returnValue) {
        var classifierOptions = getTypedValue(methodParams, CLASSIFIER_OPTIONS_PARAM, ClassifierOptions.class);
        var classifierInfoDto = classifierOptionsProcessor.processClassifierOptions(classifierOptions);
        String commaSeparatedOptions = getCommaSeparatedOptions(classifierInfoDto);
        return Map.of(CLASSIFIER_INPUT_OPTIONS_DETAILS, commaSeparatedOptions);
    }
}
