package com.ecaservice.base.model.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.SerializationUtils;
import weka.core.Instances;

import java.io.IOException;
import java.util.Base64;

/**
 * Instances json deserializer.
 *
 * @author Roman Batygin
 */
public class InstancesDeserializer extends JsonDeserializer<Instances> {

    @Override
    public Instances deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        byte[] dataBytes = Base64.getDecoder().decode(jsonNode.textValue());
        return (Instances) SerializationUtils.deserialize(dataBytes);
    }
}
