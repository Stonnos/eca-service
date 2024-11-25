package com.ecaservice.data.loader.validation;

import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import com.google.common.math.DoubleMath;
import eca.data.file.model.AttributeModel;
import eca.data.file.model.AttributeType;
import eca.data.file.model.InstancesModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.ecaservice.data.loader.validation.InstancesValidatorOrders.DATA_VALIDATOR_ORDER;

/**
 * Instances data validator.
 *
 * @author Roman Batygin
 */
@Component
@Order(DATA_VALIDATOR_ORDER)
@RequiredArgsConstructor
public class InstancesDataValidator implements InstancesValidator {

    @Override
    public void validate(InstancesModel instancesModel) {
        int numAttributes = instancesModel.getAttributes().size();
        IntStream.range(0, instancesModel.getInstances().size()).forEach(rowIdx -> {
            var instanceModel = instancesModel.getInstances().get(rowIdx);
            int actualValuesSize = Optional.ofNullable(instanceModel.getValues()).map(List::size).orElse(0);
            if (actualValuesSize != numAttributes) {
                String errorMessage =
                        String.format("Instance values list must contains [%d] values! Actual is [%d]. Row index [%d]",
                                numAttributes, actualValuesSize, rowIdx);
                throw new InvalidTrainDataFormatException(errorMessage);
            }
            IntStream.range(0, instanceModel.getValues().size()).forEach(attrIdx -> {
                AttributeModel attributeModel = instancesModel.getAttributes().get(attrIdx);
                Double value = instanceModel.getValues().get(attrIdx);
                validateValue(attributeModel, value, rowIdx, attrIdx);
            });
        });
    }

    private void validateValue(AttributeModel attributeModel, Double value, int rowIdx, int attrIdx) {

        if (AttributeType.NOMINAL.equals(attributeModel.getType()) && value != null
                && !isValidNominalCode(value, attributeModel)) {
            String errorMessage = String.format(
                    "Invalid nominal value [%s] at row [%d], attribute [%s] index [%d]. Value must be integer value in interval [%d, %d]",
                    value, rowIdx, attributeModel.getName(), attrIdx, 0, attributeModel.getValues().size() - 1);
            throw new InvalidTrainDataFormatException(errorMessage);
        }
    }

    private boolean isValidNominalCode(Double value, AttributeModel attributeModel) {
        return DoubleMath.isMathematicalInteger(value) && value >= 0 && value < attributeModel.getValues().size();
    }
}
