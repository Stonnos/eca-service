package com.ecaservice.data.loader.validation;

import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import eca.data.file.model.InstancesModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Instances validation service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class InstancesValidationService {

    private final List<InstancesValidator> instancesValidators;

    /**
     * Validates instances model.
     *
     * @param instancesModel - instances model
     * @throws InvalidTrainDataFormatException in case if train data has invalid format
     */
    public void validate(InstancesModel instancesModel) {
        instancesValidators.forEach(instancesValidator -> instancesValidator.validate(instancesModel));
    }
}
