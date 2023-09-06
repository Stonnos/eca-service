package com.ecaservice.data.loader.util;

import com.ecaservice.data.loader.exception.InvalidTrainDataFormatException;
import eca.data.file.model.AttributeModel;
import eca.data.file.model.AttributeType;
import eca.data.file.model.InstancesModel;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Validates instances model.
     *
     * @param instancesModel - instances model
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
}
