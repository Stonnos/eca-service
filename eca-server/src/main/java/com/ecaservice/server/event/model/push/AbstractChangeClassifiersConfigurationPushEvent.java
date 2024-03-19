package com.ecaservice.server.event.model.push;

import com.ecaservice.core.push.client.event.model.AbstractUserPushNotificationEvent;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import lombok.Getter;

/**
 * Change classifiers configuration push event.
 *
 * @author Roman Batygin
 */
@Getter
public abstract class AbstractChangeClassifiersConfigurationPushEvent extends AbstractUserPushNotificationEvent {

    private final ClassifiersConfiguration classifiersConfiguration;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                   - the object on which the event initially occurred or with which the event
     *                                 is associated (never {@code null})
     * @param initiator                - initiator (user)
     * @param classifiersConfiguration - classifiers configuration
     */
    protected AbstractChangeClassifiersConfigurationPushEvent(Object source,
                                                              String initiator,
                                                              ClassifiersConfiguration classifiersConfiguration) {
        super(source, initiator);
        this.classifiersConfiguration = classifiersConfiguration;
    }
}
