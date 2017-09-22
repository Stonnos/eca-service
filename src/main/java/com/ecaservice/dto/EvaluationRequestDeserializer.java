package com.ecaservice.dto;

import com.ecaservice.model.entity.EvaluationMethod;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.SerializationUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.IOException;
import java.util.Base64;

/**
 * Evaluation request json deserializer.
 * @author Roman Batygin
 */

public class EvaluationRequestDeserializer extends JsonDeserializer<EvaluationRequestDto> {

    @Override
    public EvaluationRequestDto deserialize(JsonParser jsonParser,
                                            DeserializationContext context) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        String classifierStr = jsonNode.get("classifier").textValue();
        byte[] classifierBytes = Base64.getDecoder().decode(classifierStr);
        String dataStr = jsonNode.get("data").textValue();
        byte[] dataBytes = Base64.getDecoder().decode(dataStr);
        evaluationRequestDto.setClassifier((AbstractClassifier) SerializationUtils.deserialize(classifierBytes));
        evaluationRequestDto.setData((Instances) SerializationUtils.deserialize(dataBytes));
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.valueOf(jsonNode.get("evaluationMethod").textValue()));
        Number numFolds = jsonNode.get("numFolds").numberValue();
        Number numTests = jsonNode.get("numTests").numberValue();
        evaluationRequestDto.setNumFolds(numFolds != null ? numFolds.intValue() : null);
        evaluationRequestDto.setNumTests(numTests != null ? numTests.intValue() : null);

        return evaluationRequestDto;
    }
}
