package com.ecaservice.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import weka.core.Instances;

/**
 * Component for converting instances to json format.
 *
 * @author Roman Batygin
 */
public class InstancesJsonConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final InstancesConverter INSTANCES_CONVERTER = new InstancesConverter();

    /**
     * Converts instances to json.
     *
     * @param instances - instances object
     * @return instances json
     */
    public String convert(Instances instances) {
        try {
            InstancesModel instancesModel = INSTANCES_CONVERTER.convert(instances);
            return OBJECT_MAPPER.writeValueAsString(instancesModel);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
}
