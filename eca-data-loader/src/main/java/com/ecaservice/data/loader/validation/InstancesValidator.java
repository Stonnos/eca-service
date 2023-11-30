package com.ecaservice.data.loader.validation;

import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import eca.data.file.model.InstancesModel;

/**
 * Instances validator.
 *
 * @author Roman Batygin
 */
public interface InstancesValidator {

    /**
     * Validates instances model.
     *
     * @param instancesModel - instances model
     * @throws InvalidTrainDataFormatException in case if train data has invalid format
     */
    void validate(InstancesModel instancesModel);
}
