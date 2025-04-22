package com.ecaservice.server.service.ers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.service.ers.route.AbstractEvaluationResultsRequestPathHandler;
import com.ecaservice.web.dto.model.RoutePathDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Evaluation results request path service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResultsRequestPathService {

    private final List<AbstractEvaluationResultsRequestPathHandler> abstractEvaluationResultsRequestPathHandlers;
    private final ErsRequestRepository ersRequestRepository;

    /**
     * Gets evaluation request route path for specified evaluation results.
     *
     * @param resultId - evaluation results id
     * @return route path dto
     */
    @SuppressWarnings("unchecked")
    public RoutePathDto getEvaluationResultsRequestPath(String resultId) {
        log.info("Gets evaluation request route path for evaluation results [{}]", resultId);
        var ersRequest = ersRequestRepository.findByRequestId(resultId)
                .orElseThrow(() -> new EntityNotFoundException(ErsRequest.class, resultId));
        AbstractEvaluationResultsRequestPathHandler pathHandler = abstractEvaluationResultsRequestPathHandlers
                .stream()
                .filter(handler -> handler.canHandle(ersRequest))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported operation for ers request %s",
                        ersRequest.getClass().getSimpleName()))
                );
        String path = pathHandler.handlePath(ersRequest);
        log.info("Evaluation request route path [{}] has been fetched for evaluation results [{}]", path, resultId);
        return new RoutePathDto(path);
    }
}
