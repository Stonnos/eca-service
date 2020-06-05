package com.ecaservice.service.experiment.visitor;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatusVisitor;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailTemplateType;
import com.ecaservice.service.experiment.dictionary.TemplateVariablesDictionary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Visitor obtaining email template context for experiment status.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class EmailTemplateVisitor implements RequestStatusVisitor<EmailRequest, Experiment> {

    private static final String DOWNLOAD_PATH_FORMAT = "%s/eca-api/experiment/download/%s";

    private final ExperimentConfig experimentConfig;

    @Override
    public EmailRequest caseNew(Experiment parameter) {
        return createEmailCommonRequest(parameter, EmailTemplateType.NEW_EXPERIMENT_TEMPLATE);
    }

    @Override
    public EmailRequest caseFinished(Experiment parameter) {
        EmailRequest emailRequest = createEmailCommonRequest(parameter, EmailTemplateType.FINISHED_EXPERIMENT_TEMPLATE);
        emailRequest.getEmailMessageVariables().put(TemplateVariablesDictionary.DOWNLOAD_URL_KEY,
                String.format(DOWNLOAD_PATH_FORMAT, experimentConfig.getDownloadBaseUrl(), parameter.getToken()));
        return emailRequest;
    }

    @Override
    public EmailRequest caseTimeout(Experiment parameter) {
        EmailRequest emailRequest = createEmailCommonRequest(parameter, EmailTemplateType.TIMEOUT_EXPERIMENT_TEMPLATE);
        emailRequest.getEmailMessageVariables().put(TemplateVariablesDictionary.TIMEOUT_KEY,
                experimentConfig.getTimeout());
        return emailRequest;
    }

    @Override
    public EmailRequest caseError(Experiment parameter) {
        return createEmailCommonRequest(parameter, EmailTemplateType.ERROR_EXPERIMENT_TEMPLATE);
    }

    private Map<String, Object> createCommonVariablesMap(Experiment parameter) {
        Map<String, Object> variablesMap = newHashMap();
        variablesMap.put(TemplateVariablesDictionary.FIRST_NAME_KEY, parameter.getFirstName());
        variablesMap.put(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY,
                parameter.getExperimentType().getDescription());
        variablesMap.put(TemplateVariablesDictionary.REQUEST_ID_KEY, parameter.getRequestId());
        return variablesMap;
    }

    private EmailRequest createEmailCommonRequest(Experiment experiment, EmailTemplateType templateType) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setReceiver(experiment.getEmail());
        emailRequest.setTemplateType(templateType);
        Map<String, Object> variablesMap = createCommonVariablesMap(experiment);
        emailRequest.setEmailMessageVariables(variablesMap);
        emailRequest.setHtml(true);
        return emailRequest;
    }
}
