package com.ecaservice.server.util;

import com.ecaservice.server.exception.EcaServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import eca.filter.ConstantAttributesFilter;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

/**
 * Instances utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class InstancesUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final InstancesConverter INSTANCES_CONVERTER = new InstancesConverter();

    /**
     * Converts instances to json.
     *
     * @param instances - instances object
     * @return instances json
     */
    public static String toJson(Instances instances) {
        try {
            InstancesModel instancesModel = INSTANCES_CONVERTER.convert(instances);
            return OBJECT_MAPPER.writeValueAsString(instancesModel);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    /**
     * Removes constant attributes from specified instances.
     *
     * @param instances - instances object
     * @return result - instances object
     */
    public static Instances removeConstantAttributes(Instances instances) {
        try {
            ConstantAttributesFilter filter = new ConstantAttributesFilter();
            return filter.filterInstances(instances);
        } catch (Exception ex) {
            throw new EcaServiceException(ex.getMessage());
        }
    }
}
