package com.ecaservice.data.loader.validation;

import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import eca.data.file.model.AttributeType;
import eca.data.file.model.InstancesModel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static com.ecaservice.data.loader.validation.InstancesValidatorOrders.CLASS_VALIDATOR_ORDER;

/**
 * Instances class validator.
 *
 * @author Roman Batygin
 */
@Component
@Order(CLASS_VALIDATOR_ORDER)
public class InstancesClassValidator implements InstancesValidator {

    @Override
    public void validate(InstancesModel instancesModel) {
        var classAttribute = instancesModel.getAttributes()
                .stream()
                .filter(attributeModel -> attributeModel.getName().equals(instancesModel.getClassName()))
                .findFirst()
                .orElseThrow(() -> new InvalidTrainDataFormatException(
                        String.format("Class attribute [%s] not found in instances [%s] attributes set",
                                instancesModel.getClassName(), instancesModel.getRelationName())));
        if (!AttributeType.NOMINAL.equals(classAttribute.getType())) {
            throw new InvalidTrainDataFormatException(String.format("Class attribute [%s] must be nominal",
                    classAttribute.getName()));
        }
        if (CollectionUtils.isEmpty(classAttribute.getValues()) || classAttribute.getValues().size() == 1) {
            throw new InvalidTrainDataFormatException(String.format("Class attribute [%s] must have at least 2 values",
                    classAttribute.getName()));
        }
    }
}
