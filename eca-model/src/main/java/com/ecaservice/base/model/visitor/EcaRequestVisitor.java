package com.ecaservice.base.model.visitor;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.InstancesRequest;

/**
 * Eca request visitor interface.
 *
 * @author Roman Batygin
 */
public interface EcaRequestVisitor {

    /**
     * Visit evaluation request.
     *
     * @param evaluationRequest - evaluation request
     */
    void visitEvaluationRequest(EvaluationRequest evaluationRequest);

    /**
     * Visit experiment request.
     *
     * @param experimentRequest - experiment request
     */
    void visitExperimentRequest(ExperimentRequest experimentRequest);

    /**
     * Visit instances request.
     *
     * @param instancesRequest - instances request
     */
    void visitInstancesRequest(InstancesRequest instancesRequest);
}
