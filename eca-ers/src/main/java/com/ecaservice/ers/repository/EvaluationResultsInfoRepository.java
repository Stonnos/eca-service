package com.ecaservice.ers.repository;

import com.ecaservice.ers.model.EvaluationResultsInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository to manage with {@link EvaluationResultsInfo} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsInfoRepository
        extends JpaRepository<EvaluationResultsInfo, Long>, JpaSpecificationExecutor<EvaluationResultsInfo> {

    /**
     * Checks existing of row with specified request id.
     *
     * @param requestId - request id
     * @return {@code true} if there is row with specified request id
     */
    boolean existsByRequestId(String requestId);

    /**
     * Finds evaluation results info by request id
     *
     * @param requestId - request id
     * @return evaluation results info
     */
    @EntityGraph(value = "evaluationResults", type = EntityGraph.EntityGraphType.FETCH)
    EvaluationResultsInfo findByRequestId(String requestId);
}
