package com.ecaservice.data.loader.util;

import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import eca.data.file.model.AttributeModel;
import eca.data.file.model.AttributeType;
import eca.data.file.model.InstancesModel;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Instances validation utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class InstancesValidationUtils {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    /**
     * Validates instances model.
     *
     * @param instancesModel - instances model
     * @throws InvalidTrainDataFormatException in case if train data has invalid format
     */
    public static void validateInstances(InstancesModel instancesModel) {
        if (StringUtils.isEmpty(instancesModel.getRelationName())) {
            throw new InvalidTrainDataFormatException("Got empty relation name");
        }
        if (CollectionUtils.isEmpty(instancesModel.getAttributes())) {
            throw new InvalidTrainDataFormatException("Got empty attributes list");
        }
        if (CollectionUtils.isEmpty(instancesModel.getInstances())) {
            throw new InvalidTrainDataFormatException("Got empty data list");
        }
        validateAttributes(instancesModel);
        validateData(instancesModel);
        validateClass(instancesModel);
    }

    private static void validateClass(InstancesModel instancesModel) {
        if (StringUtils.isEmpty(instancesModel.getClassName())) {
            throw new InvalidTrainDataFormatException("Got empty class name");
        }
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

    private static void validateData(InstancesModel instancesModel) {
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

    private static void validateValue(AttributeModel attributeModel, String value, int rowIdx, int attrIdx) {
        if (StringUtils.isNotEmpty(value)) {
            switch (attributeModel.getType()) {
                case NUMERIC:
                    if (!NumberUtils.isParsable(value)) {
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
                                value, rowIdx, attributeModel.getName(), attrIdx, DATE_TIME_FORMAT);
                        throw new InvalidTrainDataFormatException(errorMessage);
                    }
                    break;
                default:
                    throw new IllegalStateException(String.format("Unsupported attribute type [%s]",
                            attributeModel.getType()));
            }
        }
    }

    private static void validateAttributes(InstancesModel instancesModel) {
        IntStream.range(0, instancesModel.getAttributes().size()).forEach(
                i -> validateAttribute(instancesModel.getAttributes().get(i), i));
    }

    private static void validateAttribute(AttributeModel attributeModel, int index) {
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
                !DATE_TIME_FORMAT.equals(attributeModel.getDateFormat())) {
            throw new InvalidTrainDataFormatException(
                    String.format("Invalid date format [%s] for attribute [%s]. Must be [%s]",
                            attributeModel.getDateFormat(), attributeModel.getName(), DATE_TIME_FORMAT));
        }
    }

    private static void validateUniqueNominalValues(AttributeModel attributeModel) {
        Set<String> uniqueValues = newHashSet();
        for (var value : attributeModel.getValues()) {
            if (!uniqueValues.add(value)) {
                throw new InvalidTrainDataFormatException(
                        String.format("Attribute [%s] has duplicate values with code [%s]. Values must be unique",
                                attributeModel.getName(), value));
            }
        }
    }

    private static boolean isValidDateTime(String value) {
        try {
            LocalDateTime.parse(value, DATE_TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
