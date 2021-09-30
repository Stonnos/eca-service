package com.ecaservice.server.model;

import com.ecaservice.server.model.entity.Channel;
import lombok.Builder;
import lombok.Data;

/**
 * Message properties.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class MsgProperties {

    /**
     * Channel type
     */
    private Channel channel;

    /**
     * Reply to queue
     */
    private String replyTo;

    /**
     * MQ message correlation id
     */
    private String correlationId;
}
