package com.ecaservice.base.model.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import eca.core.evaluation.EvaluationResults;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.Base64;

/**
 * Evaluation results json deserializer.
 *
 * @author Roman Batygin
 */
public class EvaluationResultsDeserializer extends JsonDeserializer<EvaluationResults> {

    @Override
    public EvaluationResults deserialize(JsonParser parser,
                                         DeserializationContext context) throws IOException {
        JsonNode jsonNode = parser.getCodec().readTree(parser);
        if (!jsonNode.isNull()) {
            byte[] bytes = Base64.getDecoder().decode(jsonNode.textValue());
            return (EvaluationResults) SerializationUtils.deserialize(bytes);
        }
        return null;
    }
}
