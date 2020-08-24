package com.ecaservice.base.model.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import eca.core.evaluation.EvaluationResults;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Base64;

/**
 * Evaluation results json serializer.
 *
 * @author Roman Batygin
 */
public class EvaluationResultsSerializer extends JsonSerializer<EvaluationResults> {

    @Override
    public void serialize(EvaluationResults evaluationResults,
                          JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {
        if (evaluationResults != null) {
            byte[] bytes = SerializationUtils.serialize(evaluationResults);
            jsonGenerator.writeString(Base64.getEncoder().encodeToString(bytes));
        } else {
            jsonGenerator.writeNull();
        }
    }
}
