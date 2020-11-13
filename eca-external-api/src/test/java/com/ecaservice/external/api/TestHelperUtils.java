package com.ecaservice.external.api;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.amqp.core.MessageProperties;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String TEST_REQUEST_JSON = "test-request.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String REPLY_TO = "reply-to";

    /**
     * Creates evaluation request dto.
     *
     * @return evaluation request dto
     */
    @SneakyThrows
    public static EvaluationRequestDto createEvaluationRequestDto() {
        @Cleanup InputStream inputStream =
                TestHelperUtils.class.getClassLoader().getResourceAsStream(TEST_REQUEST_JSON);
        return OBJECT_MAPPER.readValue(inputStream, EvaluationRequestDto.class);
    }

    /**
     * Creates message properties.
     *
     * @return message properties
     */
    public static MessageProperties buildMessageProperties() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setReplyTo(REPLY_TO);
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        return messageProperties;
    }

    /**
     * Creates evaluation request entity.
     *
     * @param correlationId - correlation id
     * @return evaluation request entity
     */
    public static EvaluationRequestEntity createEvaluationRequestEntity(String correlationId) {
        EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
        evaluationRequestEntity.setRequestStage(RequestStageType.NOT_SEND);
        evaluationRequestEntity.setCreationDate(LocalDateTime.now());
        evaluationRequestEntity.setCorrelationId(correlationId);
        return evaluationRequestEntity;
    }
}
