package com.ecaservice.server.bpm.extract;

import com.ecaservice.server.event.model.push.PushMessageParams;
import com.ecaservice.server.service.push.dictionary.ExperimentPushProperty;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.server.bpm.CamundaVariables.PUSH_MESSAGE_PROPERTIES;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_MESSAGE_TYPE;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_TEMPLATE_CODE;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_TEMPLATE_CONTEXT_VARIABLE;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Camunda helper.
 *
 * @author Roman Batygin
 */
@Component
public class PushMessageParamsExtractor {

    private static final String COMMA_SEPARATOR = ",";

    /**
     * Extracts push message params from execution.
     *
     * @param execution - delegate execution
     * @return push message params
     */
    public PushMessageParams extractPushMessageParams(DelegateExecution execution) {
        String messageType = getVariable(execution, PUSH_MESSAGE_TYPE, String.class);
        String templateCode = getVariable(execution, PUSH_TEMPLATE_CODE, String.class);
        String messageTemplateContextVariable = getVariable(execution, PUSH_TEMPLATE_CONTEXT_VARIABLE, String.class);
        List<ExperimentPushProperty> messageProperties = getMessageProperties(execution);
        return PushMessageParams.builder()
                .messageType(messageType)
                .templateCode(templateCode)
                .templateContextVariable(messageTemplateContextVariable)
                .messageProperties(messageProperties)
                .build();
    }

    private List<ExperimentPushProperty> getMessageProperties(DelegateExecution execution) {
        String messagePropertiesValue = getVariable(execution, PUSH_MESSAGE_PROPERTIES, String.class);
        String[] messageProperties = StringUtils.split(messagePropertiesValue, COMMA_SEPARATOR);
        return Stream.of(messageProperties)
                .map(ExperimentPushProperty::valueOf)
                .collect(Collectors.toList());
    }
}
