package com.ecaservice.server.service.experiment.visitor;

import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatusVisitor;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static com.ecaservice.server.util.Utils.buildExperimentDownloadUrl;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Visitor obtaining email template context for experiment status.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class EmailTemplateVisitor implements RequestStatusVisitor<EmailRequest, Experiment> {

    private final ExperimentConfig experimentConfig;

    @Override
    public EmailRequest caseNew(Experiment parameter) {
        return createEmailCommonRequest(parameter, Templates.NEW_EXPERIMENT);
    }

    @Override
    public EmailRequest caseFinished(Experiment parameter) {
        EmailRequest emailRequest = createEmailCommonRequest(parameter, Templates.FINISHED_EXPERIMENT);
        String downloadUrl = buildExperimentDownloadUrl(experimentConfig.getDownloadBaseUrl(), parameter.getToken());
        emailRequest.getVariables().put(TemplateVariablesDictionary.DOWNLOAD_URL_KEY, downloadUrl);
        return emailRequest;
    }

    @Override
    public EmailRequest caseTimeout(Experiment parameter) {
        EmailRequest emailRequest = createEmailCommonRequest(parameter, Templates.TIMEOUT_EXPERIMENT);
        emailRequest.getVariables().put(TemplateVariablesDictionary.TIMEOUT_KEY,
                String.valueOf(experimentConfig.getTimeout()));
        return emailRequest;
    }

    @Override
    public EmailRequest caseError(Experiment parameter) {
        return createEmailCommonRequest(parameter, Templates.ERROR_EXPERIMENT);
    }

    @Override
    public EmailRequest caseInProgress(Experiment parameter) {
        return createEmailCommonRequest(parameter, Templates.IN_PROGRESS_EXPERIMENT);
    }

    private Map<String, String> createCommonVariablesMap(Experiment parameter) {
        Map<String, String> variablesMap = newHashMap();
        variablesMap.put(TemplateVariablesDictionary.FIRST_NAME_KEY, parameter.getFirstName());
        variablesMap.put(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY,
                parameter.getExperimentType().getDescription());
        variablesMap.put(TemplateVariablesDictionary.REQUEST_ID_KEY, parameter.getRequestId());
        return variablesMap;
    }

    private EmailRequest createEmailCommonRequest(Experiment experiment, String templateCode) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setReceiver(experiment.getEmail());
        emailRequest.setTemplateCode(templateCode);
        Map<String, String> variablesMap = createCommonVariablesMap(experiment);
        emailRequest.setVariables(variablesMap);
        emailRequest.setPriority(MEDIUM);
        return emailRequest;
    }
}
