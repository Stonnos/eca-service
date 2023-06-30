package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;

/**
 * Optimal classifier options fetcher interface.
 *
 * @author Roman Batygin
 */
public interface OptimalClassifierOptionsFetcher {

    /**
     * Gets optimal classifiers options.
     *
     * @param instancesRequestDataModel - instances request data model
     * @return classifier options result
     */
    ClassifierOptionsResult getOptimalClassifierOptions(InstancesRequestDataModel instancesRequestDataModel);
}
