package com.ecaservice.base.model.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.SerializationUtils;
import weka.core.Instances;

import java.io.IOException;
import java.util.Base64;

/**
 * Instances serializer.
 *
 * @author Roman Batygin
 */
public class InstancesSerializer extends JsonSerializer<Instances> {

    @Override
    public void serialize(Instances instances,
                          JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {
        byte[] dataBytes = SerializationUtils.serialize(instances);
        jsonGenerator.writeString(Base64.getEncoder().encodeToString(dataBytes));
    }
}
