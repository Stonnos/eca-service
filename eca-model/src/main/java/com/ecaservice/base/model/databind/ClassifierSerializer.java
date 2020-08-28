package com.ecaservice.base.model.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.SerializationUtils;
import weka.classifiers.AbstractClassifier;

import java.io.IOException;
import java.util.Base64;

/**
 * Classifier serializer.
 *
 * @author Roman Batygin
 */
public class ClassifierSerializer extends JsonSerializer<AbstractClassifier> {

    @Override
    public void serialize(AbstractClassifier classifier,
                          JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {
        byte[] classifierBytes = SerializationUtils.serialize(classifier);
        jsonGenerator.writeString(Base64.getEncoder().encodeToString(classifierBytes));
    }
}
