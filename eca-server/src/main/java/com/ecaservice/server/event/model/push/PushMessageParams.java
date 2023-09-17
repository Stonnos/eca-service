package com.ecaservice.server.event.model.push;

import com.ecaservice.server.service.push.dictionary.ExperimentPushProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Push message params model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class PushMessageParams {

    /**
     * Push message type
     */
    private String messageType;

    /**
     * Push message template code
     */
    private String templateCode;

    /**
     * Push message template context variable
     */
    private String templateContextVariable;

    /**
     * Push message additional properties names
     */
    private List<ExperimentPushProperty> messageProperties;
}
