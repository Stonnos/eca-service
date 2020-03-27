package com.ecaservice.service.experiment.visitor;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatusVisitor;
import com.ecaservice.service.experiment.dictionary.TemplateVariablesDictionary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

/**
 * Visitor obtaining email template context for experiment status.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class EmailTemplateVisitor implements RequestStatusVisitor<Context, Experiment> {

    private final ExperimentConfig experimentConfig;

    @Override
    public Context caseNew(Experiment parameter) {
        return createCommonContext(parameter);
    }

    @Override
    public Context caseFinished(Experiment parameter) {
        Context context = createCommonContext(parameter);
        context.setVariable(TemplateVariablesDictionary.DOWNLOAD_URL_KEY,
                String.format(experimentConfig.getDownloadUrl(), parameter.getToken()));
        return context;
    }

    @Override
    public Context caseTimeout(Experiment parameter) {
        Context context = createCommonContext(parameter);
        context.setVariable(TemplateVariablesDictionary.TIMEOUT_KEY, experimentConfig.getTimeout());
        return context;
    }

    @Override
    public Context caseError(Experiment parameter) {
        return createCommonContext(parameter);
    }

    private Context createCommonContext(Experiment parameter) {
        Context context = new Context();
        context.setVariable(TemplateVariablesDictionary.FIRST_NAME_KEY, parameter.getFirstName());
        context.setVariable(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY,
                parameter.getExperimentType().getDescription());
        context.setVariable(TemplateVariablesDictionary.REQUEST_ID_KEY, parameter.getRequestId());
        return context;
    }
}
