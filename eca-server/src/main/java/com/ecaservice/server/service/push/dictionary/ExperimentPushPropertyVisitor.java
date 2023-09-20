package com.ecaservice.server.service.push.dictionary;


import com.ecaservice.server.model.entity.Experiment;

/**
 * Experiment push property visitor.
 *
 * @author Roman Batygin
 */
public interface ExperimentPushPropertyVisitor {

    /**
     * Visits experiment id.
     *
     * @param experiment - experiment entity
     * @return experiment id value
     */
    String visitId(Experiment experiment);

    /**
     * Visits experiment request id.
     *
     * @param experiment - experiment entity
     * @return experiment request id value
     */
    String visitRequestId(Experiment experiment);

    /**
     * Visits experiment request status.
     *
     * @param experiment - experiment entity
     * @return experiment request status value
     */
    String visitRequestStatus(Experiment experiment);
}
