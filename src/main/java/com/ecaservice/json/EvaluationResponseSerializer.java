package com.ecaservice.json;

import com.ecaservice.dto.EvaluationResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Base64;

/**
 * {@link EvaluationResponse} json serializer.
 *
 * @author Roman Batygin
 */

public class EvaluationResponseSerializer extends JsonSerializer<EvaluationResponse> {

    @Override
    public void serialize(EvaluationResponse evaluationResponse,
                          JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {

        jsonGenerator.writeStartObject();
        if (evaluationResponse.getEvaluationResults() != null) {
            byte[] bytes = SerializationUtils.serialize(evaluationResponse.getEvaluationResults());
            jsonGenerator.writeStringField("evaluationResults", Base64.getEncoder().encodeToString(bytes));
        } else {
            jsonGenerator.writeNullField("evaluationResults");
        }
        jsonGenerator.writeStringField("status", evaluationResponse.getStatus().name());
        jsonGenerator.writeStringField("errorMessage", evaluationResponse.getErrorMessage());
        jsonGenerator.writeEndObject();
    }
}
