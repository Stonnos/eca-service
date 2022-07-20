package com.ecaservice.server.service.experiment.visitor;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatusVisitor;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static com.ecaservice.notification.util.Priority.MEDIUM;
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
    public EmailRequest caseNew(Experiment experiment) {
        return createEmailCommonRequest(experiment, Templates.NEW_EXPERIMENT);
    }

    @Override
    public EmailRequest caseFinished(Experiment experiment) {
        EmailRequest emailRequest = createEmailCommonRequest(experiment, Templates.FINISHED_EXPERIMENT);
        emailRequest.getVariables().put(TemplateVariablesDictionary.DOWNLOAD_URL_KEY,
                experiment.getExperimentDownloadUrl());
        return emailRequest;
    }

    @Override
    public EmailRequest caseTimeout(Experiment experiment) {
        EmailRequest emailRequest = createEmailCommonRequest(experiment, Templates.TIMEOUT_EXPERIMENT);
        emailRequest.getVariables().put(TemplateVariablesDictionary.TIMEOUT_KEY,
                String.valueOf(experimentConfig.getTimeout()));
        return emailRequest;
    }

    @Override
    public EmailRequest caseError(Experiment experiment) {
        return createEmailCommonRequest(experiment, Templates.ERROR_EXPERIMENT);
    }

    @Override
    public EmailRequest caseInProgress(Experiment experiment) {
        return createEmailCommonRequest(experiment, Templates.IN_PROGRESS_EXPERIMENT);
    }

    private Map<String, String> createCommonVariablesMap(Experiment experiment) {
        Map<String, String> variablesMap = newHashMap();
        variablesMap.put(TemplateVariablesDictionary.FIRST_NAME_KEY, experiment.getFirstName());
        variablesMap.put(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY,
                experiment.getExperimentType().getDescription());
        variablesMap.put(TemplateVariablesDictionary.REQUEST_ID_KEY, experiment.getRequestId());
        return variablesMap;
    }

    private EmailRequest createEmailCommonRequest(Experiment experiment, String templateCode) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRequestId(UUID.randomUUID().toString());
        emailRequest.setReceiver(experiment.getEmail());
        emailRequest.setTemplateCode(templateCode);
        Map<String, String> variablesMap = createCommonVariablesMap(experiment);
        emailRequest.setVariables(variablesMap);
        emailRequest.setPriority(MEDIUM);
        return emailRequest;
    }
}
