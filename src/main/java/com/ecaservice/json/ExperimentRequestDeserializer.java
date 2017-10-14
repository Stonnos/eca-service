package com.ecaservice.json;

import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.SerializationUtils;
import weka.core.Instances;

import java.io.IOException;
import java.util.Base64;

/**
 * Experiment request json deserializer.
 *
 * @author Roman Batygin
 */
public class ExperimentRequestDeserializer extends JsonDeserializer<ExperimentRequestDto> {

    @Override
    public ExperimentRequestDto deserialize(JsonParser jsonParser,
                                            DeserializationContext context) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        ExperimentRequestDto experimentRequestDto = new ExperimentRequestDto();
        experimentRequestDto.setFirstName(jsonNode.get("firstName").textValue());
        experimentRequestDto.setEmail(jsonNode.get("email").textValue());
        experimentRequestDto.setExperimentType(ExperimentType.valueOf(jsonNode.get("experimentType").textValue()));
        String dataStr = jsonNode.get("data").textValue();
        byte[] dataBytes = Base64.getDecoder().decode(dataStr);
        experimentRequestDto.setData((Instances) SerializationUtils.deserialize(dataBytes));
        experimentRequestDto.setEvaluationMethod(EvaluationMethod.valueOf(
                jsonNode.get("evaluationMethod").textValue()));
        return experimentRequestDto;
    }
}
