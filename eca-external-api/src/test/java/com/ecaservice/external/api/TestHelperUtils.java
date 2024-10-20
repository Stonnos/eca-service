package com.ecaservice.external.api;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.dto.ExApiExperimentType;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.dto.ExperimentResultsResponseDto;
import com.ecaservice.external.api.dto.InstancesRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.amqp.core.MessageProperties;
import weka.core.Instances;

import java.io.File;
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
    private static final String DATA_PATH = "data/iris.xlsx";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String REPLY_TO = "reply-to";
    private static final String EXPERIMENT_DOWNLOAD_URL = "http://localhost:900/object-storage/experiment.model";
    private static final String MODEL_DOWNLOAD_URL = "http://localhost:900/object-storage/classifier.model";

    /**
     * Generates the test data set.
     *
     * @return created training data
     */
    @SneakyThrows
    public static Instances loadInstances() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        XLSLoader dataLoader = new XLSLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(DATA_PATH).getFile())));
        return dataLoader.loadInstances();
    }

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
     * Creates instances request dto.
     *
     * @return instances request dto
     */
    public static InstancesRequestDto createInstancesRequestDto() {
        var instancesRequestDto = new InstancesRequestDto();
        instancesRequestDto.setTrainDataUuid(UUID.randomUUID().toString());
        return instancesRequestDto;
    }

    /**
     * Creates experiment request dto.
     *
     * @return experiment request dto
     */
    public static ExperimentRequestDto createExperimentRequestDto() {
        var experimentRequestDto = new ExperimentRequestDto();
        experimentRequestDto.setTrainDataUuid(UUID.randomUUID().toString());
        experimentRequestDto.setExperimentType(ExApiExperimentType.RANDOM_FORESTS);
        experimentRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        return experimentRequestDto;
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
        evaluationRequestEntity.setRequestStage(RequestStageType.READY);
        evaluationRequestEntity.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequestEntity.setCreationDate(LocalDateTime.now());
        evaluationRequestEntity.setCorrelationId(correlationId);
        return evaluationRequestEntity;
    }

    /**
     * Creates evaluation request entity.
     *
     * @param requestStageType   - request stage type
     * @param endDate            - end date
     * @param requestTimeoutDate - request timeout date
     * @return evaluation request entity
     */
    public static EvaluationRequestEntity createEvaluationRequestEntity(RequestStageType requestStageType,
                                                                        LocalDateTime endDate,
                                                                        LocalDateTime requestTimeoutDate) {
        EvaluationRequestEntity evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestEntity.setRequestStage(requestStageType);
        evaluationRequestEntity.setEndDate(endDate);
        evaluationRequestEntity.setRequestTimeoutDate(requestTimeoutDate);
        return evaluationRequestEntity;
    }

    /**
     * Creates experiment request entity.
     *
     * @param correlationId    - correlation id
     * @param requestStageType - request stage type
     * @return experiment request entity
     */
    public static ExperimentRequestEntity createExperimentRequestEntity(String correlationId,
                                                                        RequestStageType requestStageType) {
        ExperimentRequestEntity experimentRequestEntity = new ExperimentRequestEntity();
        experimentRequestEntity.setRequestStage(requestStageType);
        experimentRequestEntity.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experimentRequestEntity.setExperimentType(ExperimentType.RANDOM_FORESTS);
        experimentRequestEntity.setCreationDate(LocalDateTime.now());
        experimentRequestEntity.setCorrelationId(correlationId);
        return experimentRequestEntity;
    }

    /**
     * Creates experiment request entity.
     *
     * @param requestStageType   - request stage type
     * @param requestTimeoutDate - request timeout date
     * @return experiment request entity
     */
    public static ExperimentRequestEntity createExperimentRequestEntity(RequestStageType requestStageType,
                                                                        LocalDateTime requestTimeoutDate) {
        ExperimentRequestEntity experimentRequestEntity = createExperimentRequestEntity(UUID.randomUUID().toString(),
                requestStageType);
        experimentRequestEntity.setRequestTimeoutDate(requestTimeoutDate);
        return experimentRequestEntity;
    }

    /**
     * Creates evaluation response with success status.
     *
     * @return evaluation response
     */
    public static EvaluationResponse successEvaluationResponse() {
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setRequestId(UUID.randomUUID().toString());
        evaluationResponse.setStatus(TechnicalStatus.SUCCESS);
        evaluationResponse.setModelUrl(MODEL_DOWNLOAD_URL);
        return evaluationResponse;
    }

    /**
     * Creates experiment response with success status.
     *
     * @return experiment response
     */
    public static ExperimentResponse successExperimentResponse() {
        ExperimentResponse evaluationResponse = new ExperimentResponse();
        evaluationResponse.setDownloadUrl(EXPERIMENT_DOWNLOAD_URL);
        evaluationResponse.setRequestId(UUID.randomUUID().toString());
        evaluationResponse.setStatus(TechnicalStatus.SUCCESS);
        return evaluationResponse;
    }

    /**
     * Creates experiment response with success status.
     *
     * @return experiment response
     */
    public static ExperimentResponse errorExperimentResponse() {
        ExperimentResponse evaluationResponse = new ExperimentResponse();
        evaluationResponse.setRequestId(UUID.randomUUID().toString());
        evaluationResponse.setStatus(TechnicalStatus.ERROR);
        return evaluationResponse;
    }

    /**
     * Creates evaluation response with error status.
     *
     * @return evaluation response
     */
    public static EvaluationResponse errorEvaluationResponse() {
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setRequestId(UUID.randomUUID().toString());
        evaluationResponse.setStatus(TechnicalStatus.ERROR);
        return evaluationResponse;
    }

    /**
     * Creates evaluation response dto.
     *
     * @param requestId        - request id
     * @param evaluationStatus - evaluation status
     * @return evaluation response dto
     */
    public static EvaluationResultsResponseDto createEvaluationResponseDto(String requestId,
                                                                           EvaluationStatus evaluationStatus) {
        return EvaluationResultsResponseDto.builder()
                .requestId(requestId)
                .evaluationStatus(evaluationStatus)
                .build();
    }

    /**
     * Creates experiment response dto.
     *
     * @param requestId        - request id
     * @param evaluationStatus - evaluation status
     * @return experiment response dto
     */
    public static ExperimentResultsResponseDto createExperimentResponseDto(String requestId,
                                                                           EvaluationStatus evaluationStatus) {
        return ExperimentResultsResponseDto.builder()
                .requestId(requestId)
                .evaluationStatus(evaluationStatus)
                .build();
    }
}
