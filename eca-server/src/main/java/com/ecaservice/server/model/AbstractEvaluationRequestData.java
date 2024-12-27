package com.ecaservice.server.model;

import com.ecaservice.server.model.entity.Channel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Abstract evaluation request data.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public abstract class AbstractEvaluationRequestData implements Serializable {

    /**
     * Request channel
     */
    private Channel channel;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Train data uuid
     */
    private String dataUuid;

    /**
     * User name
     */
    private String createdBy;

    /**
     * Reply to queue
     */
    private String replyTo;

    /**
     * MQ message correlation id
     */
    private String correlationId;
}
