package com.ecaservice.data.loader.validation;

import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import eca.data.file.model.InstancesModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static com.ecaservice.data.loader.validation.InstancesValidatorOrders.META_DATA_VALIDATOR_ORDER;

/**
 * Instances meta data validator.
 *
 * @author Roman Batygin
 */
@Component
@Order(META_DATA_VALIDATOR_ORDER)
public class InstancesMetaDataValidator implements InstancesValidator {

    @Override
    public void validate(InstancesModel instancesModel) {
        if (StringUtils.isEmpty(instancesModel.getRelationName())) {
            throw new InvalidTrainDataFormatException("Got empty relation name");
        }
        if (CollectionUtils.isEmpty(instancesModel.getAttributes())) {
            throw new InvalidTrainDataFormatException("Got empty attributes list");
        }
        if (CollectionUtils.isEmpty(instancesModel.getInstances())) {
            throw new InvalidTrainDataFormatException("Got empty data list");
        }
        if (StringUtils.isEmpty(instancesModel.getClassName())) {
            throw new InvalidTrainDataFormatException("Got empty class name");
        }
    }
}
