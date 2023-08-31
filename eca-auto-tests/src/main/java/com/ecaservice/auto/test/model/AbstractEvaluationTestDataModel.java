package com.ecaservice.auto.test.model;

import lombok.Data;

/**
 * Abstract evaluation test data model.
 *
 * @author Roman Batygin
 */
@Data
public abstract class AbstractEvaluationTestDataModel {

    /**
     * Train data path in resources directory
     */
    private String trainDataPath;
}
