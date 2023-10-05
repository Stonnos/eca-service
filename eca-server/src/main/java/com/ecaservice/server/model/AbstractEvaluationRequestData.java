package com.ecaservice.server.model;

import com.ecaservice.server.model.entity.Channel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Abstract evaluation request data model.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEvaluationRequestData implements Serializable {

    /**
     * Request channel
     */
    private final Channel channel;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Train data uuid in central data storage
     */
    private String dataUuid;
}
