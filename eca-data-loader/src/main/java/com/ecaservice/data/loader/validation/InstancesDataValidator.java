package com.ecaservice.data.loader.validation;

import com.ecaservice.data.loader.config.AppProperties;
import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import eca.data.file.model.AttributeModel;
import eca.data.file.model.InstancesModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    private final AppProperties appProperties;
    private DateTimeFormatter dateTimeFormatter;

    @PostConstruct
    public void init() {
        dateTimeFormatter = DateTimeFormatter.ofPattern(appProperties.getDateFormat());
    }

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
                String value = instanceModel.getValues().get(attrIdx);
                validateValue(attributeModel, value, rowIdx, attrIdx);
            });
        });
    }

    private void validateValue(AttributeModel attributeModel, String value, int rowIdx, int attrIdx) {
        if (StringUtils.isNotEmpty(value)) {
            switch (attributeModel.getType()) {
                case NUMERIC:
                    if (!NumberUtils.isCreatable(value)) {
                        String errorMessage =
                                String.format("Invalid numeric value [%s] at row [%d], attribute [%s] index [%d]",
                                        value, rowIdx, attributeModel.getName(), attrIdx);
                        throw new InvalidTrainDataFormatException(errorMessage);
                    }
                    break;
                case NOMINAL:
                    if (!attributeModel.getValues().contains(value)) {
                        String errorMessage = String.format(
                                "Invalid nominal value [%s] at row [%d], attribute [%s] index [%d]. Value must be one of %s",
                                value, rowIdx, attributeModel.getName(), attrIdx, attributeModel.getValues());
                        throw new InvalidTrainDataFormatException(errorMessage);
                    }
                    break;
                case DATE:
                    if (!isValidDateTime(value)) {
                        String errorMessage = String.format(
                                "Invalid date value [%s] at row [%d], attribute [%s] index [%d]. Date must be in format [%s]",
                                value, rowIdx, attributeModel.getName(), attrIdx, dateTimeFormatter);
                        throw new InvalidTrainDataFormatException(errorMessage);
                    }
                    break;
                default:
                    throw new IllegalStateException(String.format("Unsupported attribute type [%s]",
                            attributeModel.getType()));
            }
        }
    }

    private boolean isValidDateTime(String value) {
        try {
            LocalDateTime.parse(value, dateTimeFormatter);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
