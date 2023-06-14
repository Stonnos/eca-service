package com.ecaservice.server.model.experiment;

/**
 * Experiment request data visitor interface.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestDataVisitor {

    /**
     * Visit experiment web request data model.
     *
     * @param experimentWebRequestData - experiment web request data model
     */
    void visit(ExperimentWebRequestData experimentWebRequestData);

    /**
     * Visit experiment message request data model.
     *
     * @param experimentMessageRequestData - experiment message request data model
     */
    void visit(ExperimentMessageRequestData experimentMessageRequestData);
}
