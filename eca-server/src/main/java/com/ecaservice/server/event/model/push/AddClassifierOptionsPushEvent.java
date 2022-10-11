package com.ecaservice.server.event.model.push;

import com.ecaservice.server.model.entity.ClassifiersConfiguration;

/**
 * Add classifier options push event handler.
 *
 * @author Roman Batygin
 */
public class AddClassifierOptionsPushEvent extends AbstractClassifierOptionsPushEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                   - the object on which the event initially occurred or with which the event
     *                                 is associated (never {@code null})
     * @param initiator                - initiator (user)
     * @param classifiersConfiguration - classifiers configuration
     * @param classifierOptionsId      - classifier options id
     */
    public AddClassifierOptionsPushEvent(Object source,
                                         String initiator,
                                         ClassifiersConfiguration classifiersConfiguration,
                                         long classifierOptionsId) {
        super(source, initiator, classifiersConfiguration, classifierOptionsId);
    }
}
