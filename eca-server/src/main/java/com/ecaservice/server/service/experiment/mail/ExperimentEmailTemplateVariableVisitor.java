package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.server.model.entity.Experiment;

/**
 * Experiment email template variable visitor.
 *
 * @author Roman Batygin
 */
public interface ExperimentEmailTemplateVariableVisitor {

    /**
     * Visits experiment type variable.
     *
     * @param experiment - experiment entity
     * @return experiment type value
     */
    String visitExperimentType(Experiment experiment);

    /**
     * Visits download url variable.
     *
     * @param experiment - experiment entity
     * @return experiment download url value
     */
    String visitDownloadUrl(Experiment experiment);

    /**
     * Visits timeout variable.
     *
     * @param experiment - experiment entity
     * @return timeout value
     */
    String visitTimeout(Experiment experiment);

    /**
     * Visits request id variable.
     *
     * @param experiment - experiment entity
     * @return request id value
     */
    String visitRequestId(Experiment experiment);
}
