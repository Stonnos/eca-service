package com.ecaservice.server.event.model.push;

import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import lombok.Getter;

/**
 * Add classifiers configuration options push event.
 *
 * @author Roman Batygin
 */
public class AddClassifiersConfigurationOptionsPushEvent extends ChangeClassifiersConfigurationPushEvent {

    @Getter
    private final ClassifierOptionsDto classifierOptionsDto;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                   - the object on which the event initially occurred or with which the event
     *                                 is associated (never {@code null})
     * @param initiator                - initiator (user)
     * @param classifiersConfiguration - classifiers configuration
     * @param classifierOptionsDto     - classifier options dto
     */
    public AddClassifiersConfigurationOptionsPushEvent(Object source,
                                                       String initiator,
                                                       ClassifiersConfiguration classifiersConfiguration,
                                                       ClassifierOptionsDto classifierOptionsDto) {
        super(source, initiator, classifiersConfiguration);
        this.classifierOptionsDto = classifierOptionsDto;
    }
}
