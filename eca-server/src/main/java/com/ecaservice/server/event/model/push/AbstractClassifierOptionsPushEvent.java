package com.ecaservice.server.event.model.push;

import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import lombok.Getter;

/**
 * Classifier options push event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractClassifierOptionsPushEvent extends AbstractChangeClassifiersConfigurationPushEvent {

    @Getter
    private final long classifierOptionsId;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                   - the object on which the event initially occurred or with which the event
     *                                 is associated (never {@code null})
     * @param initiator                - initiator (user)
     * @param classifiersConfiguration - classifiers configuration
     * @param classifierOptionsId      - classifier options id
     */
    public AbstractClassifierOptionsPushEvent(Object source,
                                              String initiator,
                                              ClassifiersConfiguration classifiersConfiguration,
                                              long classifierOptionsId) {
        super(source, initiator, classifiersConfiguration);
        this.classifierOptionsId = classifierOptionsId;
    }
}
