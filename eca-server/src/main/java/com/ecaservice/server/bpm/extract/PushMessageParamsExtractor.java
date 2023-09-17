package com.ecaservice.server.bpm.extract;

import com.ecaservice.server.event.model.push.PushMessageParams;
import com.ecaservice.server.service.push.dictionary.ExperimentPushProperty;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.server.bpm.CamundaVariables.PUSH_MESSAGE_PROPERTIES;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_MESSAGE_TYPE;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_TEMPLATE_CODE;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_TEMPLATE_CONTEXT_VARIABLE;
import static com.ecaservice.server.util.CamundaUtils.getValuesAsArray;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Camunda helper.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class PushMessageParamsExtractor {

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
        String[] messagePropertiesArray = getValuesAsArray(execution, PUSH_MESSAGE_PROPERTIES);
        List<ExperimentPushProperty> messageProperties = Stream.of(messagePropertiesArray)
                .map(ExperimentPushProperty::valueOf)
                .collect(Collectors.toList());
        log.debug("Push message type [{}], template code [{}], properties [{}] for execution [{}]", messageType,
                templateCode, messageProperties, execution.getProcessBusinessKey());
        return PushMessageParams.builder()
                .messageType(messageType)
                .templateCode(templateCode)
                .templateContextVariable(messageTemplateContextVariable)
                .messageProperties(messageProperties)
                .build();
    }
}
