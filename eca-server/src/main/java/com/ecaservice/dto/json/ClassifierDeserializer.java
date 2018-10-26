package com.ecaservice.dto.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.SerializationUtils;
import weka.classifiers.AbstractClassifier;

import java.io.IOException;
import java.util.Base64;

/**
 * Classifier json deserializer.
 *
 * @author Roman Batygin
 */
public class ClassifierDeserializer extends JsonDeserializer<AbstractClassifier> {

    @Override
    public AbstractClassifier deserialize(JsonParser jsonParser,
                                            DeserializationContext context) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        byte[] classifierBytes = Base64.getDecoder().decode(jsonNode.textValue());
        return (AbstractClassifier) SerializationUtils.deserialize(classifierBytes);
    }
}
