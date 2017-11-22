package com.ecaservice.dto.json;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.dto.dictionary.JsonFieldsDictionary;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.SerializationUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Evaluation request json deserializer.
 *
 * @author Roman Batygin
 */

public class EvaluationRequestDeserializer extends JsonDeserializer<EvaluationRequestDto> {

    @Override
    public EvaluationRequestDto deserialize(JsonParser jsonParser,
                                            DeserializationContext context) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        String classifierStr = jsonNode.get(JsonFieldsDictionary.CLASSIFIER).textValue();
        byte[] classifierBytes = Base64.getDecoder().decode(classifierStr);
        String dataStr = jsonNode.get(JsonFieldsDictionary.DATA).textValue();
        byte[] dataBytes = Base64.getDecoder().decode(dataStr);
        evaluationRequestDto.setClassifier((AbstractClassifier) SerializationUtils.deserialize(classifierBytes));
        evaluationRequestDto.setData((Instances) SerializationUtils.deserialize(dataBytes));
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.valueOf(
                jsonNode.get(JsonFieldsDictionary.EVALUATION_METHOD).textValue()));
        JsonNode evaluationOptionsNode = jsonNode.get(JsonFieldsDictionary.EVALUATION_OPTIONS_MAP);
        if (!evaluationOptionsNode.isNull()) {
            Map<EvaluationOption, String> evaluationOptionsMap = new EnumMap<>(EvaluationOption.class);
            for (Iterator<Map.Entry<String, JsonNode>> iterator = evaluationOptionsNode.fields(); iterator.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                evaluationOptionsMap.put(EvaluationOption.valueOf(entry.getKey()), entry.getValue().textValue());
            }
            evaluationRequestDto.setEvaluationOptionsMap(evaluationOptionsMap);
        }
        return evaluationRequestDto;
    }
}
