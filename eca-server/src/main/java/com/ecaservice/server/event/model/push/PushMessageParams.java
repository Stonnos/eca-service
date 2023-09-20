package com.ecaservice.server.event.model.push;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Push message params model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private List<String> messageProperties;
}
