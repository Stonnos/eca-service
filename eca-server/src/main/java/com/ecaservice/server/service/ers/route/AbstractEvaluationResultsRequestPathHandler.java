package com.ecaservice.server.service.ers.route;

import com.ecaservice.server.model.entity.ErsRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Abstract evaluation results request path handler.
 *
 * @param <T> - ers request entity generic type
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEvaluationResultsRequestPathHandler<T extends ErsRequest> {

    private final Class<T> type;

    /**
     * Checks that ers request can handle.
     *
     * @param ersRequestEntity - ers request entity
     * @return {@code true} if ers request can be handle
     */
    public boolean canHandle(T ersRequestEntity) {
        return ersRequestEntity != null && type.isAssignableFrom(ersRequestEntity.getClass());
    }

    /**
     * Handles path for ers request
     *
     * @param ersRequestEntity - ers request entity
     * @return result path
     */
    public abstract String handlePath(T ersRequestEntity);
}
