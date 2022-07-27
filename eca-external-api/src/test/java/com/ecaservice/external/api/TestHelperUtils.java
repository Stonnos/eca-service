package com.ecaservice.external.api;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.dto.InstancesRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.evaluation.EvaluationService;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import eca.trees.CART;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.mock.web.MockMultipartFile;
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
    private static final String DATA_PATH = "data/iris.xls";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String REPLY_TO = "reply-to";
    private static final String INSTANCES_PATH = "data.model";
    private static final String TRAINING_DATA_PARAM = "trainingData";
    private static final String IRIS_XLS = "iris.xls";
    private static final String TRAIN_DATA_URL = "data://84327874";

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
     * Creates instances mock multipart file.
     *
     * @return instances mock multipart file
     */
    @SneakyThrows
    public static MockMultipartFile createInstancesMockMultipartFile() {
        return createInstancesMockMultipartFile(IRIS_XLS);
    }

    /**
     * Creates instances mock multipart file.
     *
     * @param fileName - file name
     * @return instances mock multipart file
     */
    @SneakyThrows
    public static MockMultipartFile createInstancesMockMultipartFile(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        @Cleanup InputStream inputStream = classLoader.getResourceAsStream(DATA_PATH);
        return new MockMultipartFile(TRAINING_DATA_PARAM, fileName, null, inputStream);
    }

    /**
     * Evaluation classifier and returns its evaluation results.
     *
     * @return evaluation results
     */
    @SneakyThrows
    public static EvaluationResults getEvaluationResults() {
        CART cart = new CART();
        Instances testInstances = loadInstances();
        Evaluation evaluation = EvaluationService.evaluateModel(cart, testInstances,
                EvaluationMethod.TRAINING_DATA, 0, 0, 0);
        return new EvaluationResults(cart, evaluation);
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
        instancesRequestDto.setTrainDataUrl(TRAIN_DATA_URL);
        return instancesRequestDto;
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
     * @param requestStageType - request stage type
     * @param endDate          - end date
     * @param requestDate      - request date
     * @return evaluation request entity
     */
    public static EvaluationRequestEntity createEvaluationRequestEntity(RequestStageType requestStageType,
                                                                        LocalDateTime endDate,
                                                                        LocalDateTime requestDate) {
        EvaluationRequestEntity evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestEntity.setRequestStage(requestStageType);
        evaluationRequestEntity.setEndDate(endDate);
        evaluationRequestEntity.setRequestDate(requestDate);
        return evaluationRequestEntity;
    }

    /**
     * Creates instances entity.
     *
     * @param creationDate - creation date
     * @return instances entity
     */
    public static InstancesEntity createInstancesEntity(LocalDateTime creationDate) {
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setUuid(UUID.randomUUID().toString());
        instancesEntity.setDataPath(INSTANCES_PATH);
        instancesEntity.setCreationDate(creationDate);
        return instancesEntity;
    }

    /**
     * Creates evaluation response with success status.
     *
     * @return evaluation response
     */
    public static EvaluationResponse successEvaluationResponse() {
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setEvaluationResults(getEvaluationResults());
        evaluationResponse.setRequestId(UUID.randomUUID().toString());
        evaluationResponse.setStatus(TechnicalStatus.SUCCESS);
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
    public static EvaluationResponseDto createEvaluationResponseDto(String requestId,
                                                                    EvaluationStatus evaluationStatus) {
        return EvaluationResponseDto.builder()
                .requestId(requestId)
                .evaluationStatus(evaluationStatus)
                .build();
    }
}
