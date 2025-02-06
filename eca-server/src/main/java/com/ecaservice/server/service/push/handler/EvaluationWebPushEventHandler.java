package com.ecaservice.server.service.push.handler;

import com.ecaservice.core.push.client.event.listener.handler.AbstractUserPushNotificationEventHandler;
import com.ecaservice.server.event.model.push.EvaluationWebPushEvent;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.classifiers.ClassifiersFormTemplateProvider;
import com.ecaservice.server.service.push.EvaluationPushPropertiesHandler;
import com.ecaservice.server.service.push.PushMessageProcessor;
import com.ecaservice.server.service.push.context.EvaluationPushMessageContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;

/**
 * Classifier evaluation web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationWebPushEventHandler extends AbstractUserPushNotificationEventHandler<EvaluationWebPushEvent> {

    private final ClassifiersFormTemplateProvider classifiersFormTemplateProvider;
    private final ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    private final EvaluationPushPropertiesHandler evaluationPushPropertiesHandler;
    private final PushMessageProcessor pushMessageProcessor;

    /**
     * Constructor with parameters.
     *
     * @param classifiersFormTemplateProvider - classifier template provider
     * @param classifierOptionsInfoProcessor  - classifier options processor
     * @param evaluationPushPropertiesHandler - evaluation push properties handler
     * @param pushMessageProcessor            - evaluation push message handler
     */
    public EvaluationWebPushEventHandler(ClassifiersFormTemplateProvider classifiersFormTemplateProvider,
                                         ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor,
                                         EvaluationPushPropertiesHandler evaluationPushPropertiesHandler,
                                         PushMessageProcessor pushMessageProcessor) {
        super(EvaluationWebPushEvent.class);
        this.classifiersFormTemplateProvider = classifiersFormTemplateProvider;
        this.classifierOptionsInfoProcessor = classifierOptionsInfoProcessor;
        this.evaluationPushPropertiesHandler = evaluationPushPropertiesHandler;
        this.pushMessageProcessor = pushMessageProcessor;
    }

    @Override
    protected String getCorrelationId(EvaluationWebPushEvent evaluationWebPushEvent) {
        return evaluationWebPushEvent.getEvaluationLog().getRequestId();
    }

    @Override
    protected String getMessageType(EvaluationWebPushEvent evaluationWebPushEvent) {
        return evaluationWebPushEvent.getPushMessageParams().getMessageType();
    }

    @Override
    protected String getMessageText(EvaluationWebPushEvent evaluationWebPushEvent) {
        String classifierDescription = getClassifierDescription(evaluationWebPushEvent.getEvaluationLog());
        var context = EvaluationPushMessageContext.builder()
                .requestId(evaluationWebPushEvent.getEvaluationLog().getRequestId())
                .classifierDescription(classifierDescription)
                .build();
        return pushMessageProcessor.processMessageText(evaluationWebPushEvent.getPushMessageParams(), context);
    }

    @Override
    protected Map<String, String> createAdditionalProperties(EvaluationWebPushEvent evaluationWebPushEvent) {
        return evaluationPushPropertiesHandler.processAdditionalProperties(
                evaluationWebPushEvent.getPushMessageParams(), evaluationWebPushEvent.getEvaluationLog());
    }

    @Override
    protected List<String> getReceivers(EvaluationWebPushEvent evaluationWebPushEvent) {
        return Collections.singletonList(evaluationWebPushEvent.getEvaluationLog().getCreatedBy());
    }

    private String getClassifierDescription(EvaluationLog evaluationLog) {
        var classifierOptions = parseOptions(evaluationLog.getClassifierOptions());
        var classifierTemplate = classifiersFormTemplateProvider.getTemplate(classifierOptions);
        return classifierOptionsInfoProcessor.processTemplateTitle(classifierTemplate, classifierOptions);
    }
}
