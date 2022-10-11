package com.ecaservice.server.event.model.push;

import com.ecaservice.server.model.entity.ClassifiersConfiguration;

/**
 * Rename classifiers configuration push event.
 *
 * @author Roman Batygin
 */
public class RenameClassifiersConfigurationPushEvent extends AbstractChangeClassifiersConfigurationPushEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                   - the object on which the event initially occurred or with which the event
     *                                 is associated (never {@code null})
     * @param initiator                - initiator (user)
     * @param classifiersConfiguration - classifiers configuration
     */
    public RenameClassifiersConfigurationPushEvent(Object source,
                                                   String initiator,
                                                   ClassifiersConfiguration classifiersConfiguration) {
        super(source, initiator, classifiersConfiguration);
    }
}
