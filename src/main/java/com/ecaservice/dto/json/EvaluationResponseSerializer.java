package com.ecaservice.dto.json;

import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.dictionary.JsonFieldsDictionary;
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
            jsonGenerator.writeStringField(JsonFieldsDictionary.EVALUATION_RESULTS,
                    Base64.getEncoder().encodeToString(bytes));
        } else {
            jsonGenerator.writeNullField(JsonFieldsDictionary.EVALUATION_RESULTS);
        }
        jsonGenerator.writeStringField(JsonFieldsDictionary.STATUS, evaluationResponse.getStatus().name());
        jsonGenerator.writeStringField(JsonFieldsDictionary.ERROR_MESSAGE, evaluationResponse.getErrorMessage());
        jsonGenerator.writeEndObject();
    }
}
