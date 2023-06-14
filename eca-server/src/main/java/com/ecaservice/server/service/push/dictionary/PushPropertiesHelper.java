package com.ecaservice.server.service.push.dictionary;

import com.ecaservice.server.model.entity.Experiment;
import lombok.experimental.UtilityClass;

import java.util.Map;

import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_STATUS_PROPERTY;

/**
 * Push properties helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class PushPropertiesHelper {

    /**
     * Creates experiment push properties map.
     *
     * @param experiment - experiment entity
     * @return experiment push properties map
     */
    public static Map<String, String> createExperimentProperties(Experiment experiment) {
        return Map.of(
                EXPERIMENT_ID_PROPERTY, String.valueOf(experiment.getId()),
                EXPERIMENT_REQUEST_ID_PROPERTY, experiment.getRequestId(),
                EXPERIMENT_REQUEST_STATUS_PROPERTY, experiment.getRequestStatus().name()
        );
    }
}
