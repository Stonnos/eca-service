package com.ecaservice.service.experiment.visitor;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatusVisitor;
import com.ecaservice.service.experiment.dictionary.TemplateVariablesDictionary;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import javax.inject.Inject;

/**
 * Visitor obtaining email template context for experiment status.
 *
 * @author Roman Batygin
 */
@Component
public class EmailTemplateVisitor implements ExperimentStatusVisitor<Context, Experiment> {

    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentConfig - experiment config bean
     */
    @Inject
    public EmailTemplateVisitor(ExperimentConfig experimentConfig) {
        this.experimentConfig = experimentConfig;
    }

    @Override
    public Context caseNew(Experiment parameter) {
        Context context = createCommonContext(parameter);
        context.setVariable(TemplateVariablesDictionary.EMAIL_KEY, parameter.getEmail());
        context.setVariable(TemplateVariablesDictionary.UUID_KEY, parameter.getUuid());
        return context;
    }

    @Override
    public Context caseFinished(Experiment parameter) {
        Context context = createCommonContext(parameter);
        context.setVariable(TemplateVariablesDictionary.DOWNLOAD_URL_KEY,
                String.format(experimentConfig.getDownloadUrl(), parameter.getUuid()));
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

    @Override
    public Context caseExceeded(Experiment parameter) {
        throw new UnsupportedOperationException(String.format("Can't create context for experiment: %d",
                parameter.getId()));
    }

    private Context createCommonContext(Experiment parameter) {
        Context context = new Context();
        context.setVariable(TemplateVariablesDictionary.FIRST_NAME_KEY, parameter.getFirstName());
        context.setVariable(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY,
                parameter.getExperimentType().getDescription());
        return context;
    }
}
