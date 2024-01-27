package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.AbstractChangeClassifiersConfigurationPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsProvider;
import com.ecaservice.user.profile.options.dto.UserNotificationEventOptionsDto;
import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.server.service.push.dictionary.PushProperties.CLASSIFIERS_CONFIGURATION_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE;

/**
 * Change classifiers configuration push event handler.
 *
 * @param <E> - push event generic type
 * @author Roman Batygin
 */
@Slf4j
public abstract class AbstractChangeClassifiersConfigurationPushEventHandler<E extends AbstractChangeClassifiersConfigurationPushEvent>
        extends AbstractUserPushNotificationEventHandler<E> {

    private final ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;
    private final MessageTemplateProcessor messageTemplateProcessor;
    private final UserProfileOptionsProvider userProfileOptionsProvider;

    /**
     * Constructor with parameters.
     *
     * @param clazz                                     - push event class
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     * @param userProfileOptionsProvider                - user profile options provider
     */
    protected AbstractChangeClassifiersConfigurationPushEventHandler(
            Class<E> clazz,
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor, UserProfileOptionsProvider userProfileOptionsProvider) {
        super(clazz);
        this.classifiersConfigurationHistoryRepository = classifiersConfigurationHistoryRepository;
        this.messageTemplateProcessor = messageTemplateProcessor;
        this.userProfileOptionsProvider = userProfileOptionsProvider;
    }

    @Override
    protected List<String> getReceivers(AbstractChangeClassifiersConfigurationPushEvent event) {
        var classifiersConfiguration = event.getClassifiersConfiguration();
        log.info("Starting to get receivers for classifiers configuration [{}] event [{}]",
                classifiersConfiguration.getId(), event.getClass().getSimpleName());
        var allModifiers = classifiersConfigurationHistoryRepository.getAnotherModifiers(classifiersConfiguration,
                event.getInitiator());
        if (CollectionUtils.isEmpty(allModifiers)) {
            log.warn("No one another modifier found for classifier configuration [{}] event [{}]",
                    event.getClassifiersConfiguration().getId(), event.getClass().getSimpleName());
            return Collections.emptyList();
        }
        log.info("[{}] receivers has been fetched for classifiers configuration [{}] event [{}]", allModifiers.size(),
                classifiersConfiguration.getId(), event.getClass().getSimpleName());
        List<String> resultReceivers = allModifiers.stream()
                .filter(this::isWebPushEnabled)
                .collect(Collectors.toList());
        log.info("Classifiers configuration [{}] event [{}] result receivers: {}", classifiersConfiguration.getId(),
                event.getClass().getSimpleName(), resultReceivers);
        return allModifiers;
    }

    @Override
    protected String getMessageType(AbstractChangeClassifiersConfigurationPushEvent event) {
        return CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE;
    }

    @Override
    protected String getMessageText(E event) {
        var templateParams = createMessageTemplateParams(event);
        return messageTemplateProcessor.process(getMessageTemplateCode(), templateParams);
    }

    @Override
    protected Map<String, String> createAdditionalProperties(E event) {
        return Collections.singletonMap(CLASSIFIERS_CONFIGURATION_ID_PROPERTY,
                String.valueOf(event.getClassifiersConfiguration().getId()));
    }

    /**
     * Gets push message template code.
     *
     * @return message template code
     */
    protected abstract String getMessageTemplateCode();

    /**
     * Creates push message template params.
     *
     * @param event - push event
     * @return message template params
     */
    protected abstract Map<String, Object> createMessageTemplateParams(E event);

    private boolean isWebPushEnabled(String user) {
        try {
            UserProfileOptionsDto userProfileOptionsDto = userProfileOptionsProvider.getUserProfileOptions(user);
            log.info("User profile [{}] options has been fetched: {}", user, userProfileOptionsDto);
            boolean classifiersConfigurationChangeNotificationEventEnabled =
                    userProfileOptionsDto.getNotificationEventOptions()
                            .stream()
                            .filter(eventOptionsDto -> UserNotificationEventType.CLASSIFIER_CONFIGURATION_CHANGE.equals(
                                    eventOptionsDto.getEventType()))
                            .findFirst()
                            .map(UserNotificationEventOptionsDto::isWebPushEnabled)
                            .orElse(false);
            return userProfileOptionsDto.isWebPushEnabled() && classifiersConfigurationChangeNotificationEventEnabled;
        } catch (Exception ex) {
            log.error("Error while get user [{}] profile options. Disabled web push by default. Error details: {}",
                    user, ex.getMessage());
            return false;
        }
    }
}
