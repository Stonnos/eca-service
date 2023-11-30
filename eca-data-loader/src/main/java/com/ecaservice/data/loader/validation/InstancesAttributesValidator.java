package com.ecaservice.data.loader.validation;

import com.ecaservice.data.loader.config.AppProperties;
import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import eca.data.file.model.AttributeModel;
import eca.data.file.model.AttributeType;
import eca.data.file.model.InstancesModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.stream.IntStream;

import static com.ecaservice.data.loader.validation.InstancesValidatorOrders.ATTRIBUTES_VALIDATOR_ORDER;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Instances attributes validator.
 *
 * @author Roman Batygin
 */
@Component
@Order(ATTRIBUTES_VALIDATOR_ORDER)
@RequiredArgsConstructor
public class InstancesAttributesValidator implements InstancesValidator {

    private final AppProperties appProperties;

    @Override
    public void validate(InstancesModel instancesModel) {
        IntStream.range(0, instancesModel.getAttributes().size()).forEach(
                i -> validateAttribute(instancesModel.getAttributes().get(i), i));
    }

    private void validateAttribute(AttributeModel attributeModel, int index) {
        if (attributeModel == null || StringUtils.isBlank(attributeModel.getName())) {
            throw new InvalidTrainDataFormatException(String.format("Got empty attribute name at index [%d]", index));
        }
        if (attributeModel.getType() == null) {
            throw new InvalidTrainDataFormatException(String.format("Got null attribute type at index [%d]", index));
        }
        if (AttributeType.NOMINAL.equals(attributeModel.getType())) {
            if (CollectionUtils.isEmpty(attributeModel.getValues())) {
                throw new InvalidTrainDataFormatException(
                        String.format("Got empty attribute [%s] values", attributeModel.getName()));
            }
            validateUniqueNominalValues(attributeModel);
        }
        if (AttributeType.DATE.equals(attributeModel.getType()) &&
                !appProperties.getDateFormat().equals(attributeModel.getDateFormat())) {
            throw new InvalidTrainDataFormatException(
                    String.format("Invalid date format [%s] for attribute [%s]. Must be [%s]",
                            attributeModel.getDateFormat(), attributeModel.getName(), appProperties.getDateFormat()));
        }
    }

    private void validateUniqueNominalValues(AttributeModel attributeModel) {
        Set<String> uniqueValues = newHashSet();
        for (var value : attributeModel.getValues()) {
            if (!uniqueValues.add(value)) {
                throw new InvalidTrainDataFormatException(
                        String.format("Attribute [%s] has duplicate values with code [%s]. Values must be unique",
                                attributeModel.getName(), value));
            }
        }
    }
}
