package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EcaRequest;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.dto.InstancesRequestDto;
import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import eca.core.evaluation.EvaluationMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;

/**
 * Evaluation API service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationApiService {

    private final EcaRequestService ecaRequestService;
    private final EcaRequestMapper ecaRequestMapper;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final RabbitSender rabbitSender;
    private final RequestStageHandler requestStageHandler;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Processes evaluation request.
     *
     * @param evaluationRequestDto - evaluation request dto.
     * @return simple evaluation response dto
     */
    public SimpleEvaluationResponseDto processRequest(EvaluationRequestDto evaluationRequestDto) {
        var ecaRequestEntity = ecaRequestService.createAndSaveEvaluationRequestEntity(evaluationRequestDto);
        log.info("Starting to process evaluation request [{}]", ecaRequestEntity.getCorrelationId());
        EvaluationRequest evaluationRequest = createEvaluationRequest(evaluationRequestDto);
        var response =
                internalProcessRequest(ecaRequestEntity, evaluationRequest, rabbitSender::sendEvaluationRequest);
        log.info("Evaluation request [{}] has been sent to eca-server", ecaRequestEntity.getCorrelationId());
        return response;
    }

    /**
     * Processes evaluation request using optimal classifier model.
     *
     * @param instancesRequestDto - instances request dto
     * @return simple evaluation response dto
     */
    public SimpleEvaluationResponseDto processRequest(InstancesRequestDto instancesRequestDto) {
        var ecaRequestEntity = ecaRequestService.createAndSaveEvaluationOptimizerRequestEntity();
        log.info("Starting to process request [{}] for optimal classifier evaluation",
                ecaRequestEntity.getCorrelationId());
        InstancesRequest instancesRequest = new InstancesRequest();
        instancesRequest.setDataUuid(instancesRequestDto.getTrainDataUuid());
        var response =
                internalProcessRequest(ecaRequestEntity, instancesRequest, rabbitSender::sendInstancesRequest);
        log.info("Optimal classifier evaluation request [{}] has been sent to eca-server",
                ecaRequestEntity.getCorrelationId());
        return response;
    }

    /**
     * Processes experiment request.
     *
     * @param experimentRequestDto - experiment request dto
     * @return simple evaluation response dto
     */
    public SimpleEvaluationResponseDto processRequest(ExperimentRequestDto experimentRequestDto) {
        var ecaRequestEntity = ecaRequestService.createAndSaveExperimentRequestEntity(experimentRequestDto);
        log.info("Starting to process experiment request [{}]", ecaRequestEntity.getCorrelationId());
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setDataUuid(experimentRequestDto.getTrainDataUuid());
        experimentRequest.setEvaluationMethod(experimentRequestDto.getEvaluationMethod());
        experimentRequest.setExperimentType(ecaRequestMapper.map(experimentRequestDto.getExperimentType()));
        var response =
                internalProcessRequest(ecaRequestEntity, experimentRequest, rabbitSender::sendExperimentRequest);
        log.info("Experiment request [{}] has been sent to eca-server", ecaRequestEntity.getCorrelationId());
        return response;
    }

    private <R extends EcaRequest> SimpleEvaluationResponseDto internalProcessRequest(EcaRequestEntity ecaRequestEntity,
                                                                                      R ecaRequest,
                                                                                      BiConsumer<R, String> ecaRequestConsumer) {
        try {
            ecaRequestConsumer.accept(ecaRequest, ecaRequestEntity.getCorrelationId());
            saveAsSent(ecaRequestEntity);
            return SimpleEvaluationResponseDto.builder()
                    .requestId(ecaRequestEntity.getCorrelationId())
                    .evaluationStatus(EvaluationStatus.IN_PROGRESS)
                    .build();
        } catch (Exception ex) {
            log.error("There was an error while request processing with correlation id [{}]: {}",
                    ecaRequestEntity.getCorrelationId(), ex.getMessage(), ex);
            return handleError(ecaRequestEntity, ex);
        }
    }

    private SimpleEvaluationResponseDto handleError(EcaRequestEntity ecaRequestEntity, Exception ex) {
        requestStageHandler.handleError(ecaRequestEntity, ex.getMessage());
        return SimpleEvaluationResponseDto.builder()
                .requestId(ecaRequestEntity.getCorrelationId())
                .evaluationStatus(EvaluationStatus.ERROR)
                .errorCode(ecaRequestEntity.getErrorCode())
                .build();
    }

    private void saveAsSent(EcaRequestEntity ecaRequestEntity) {
        ecaRequestEntity.setRequestStage(RequestStageType.REQUEST_SENT);
        ecaRequestEntity.setRequestDate(LocalDateTime.now());
        ecaRequestRepository.save(ecaRequestEntity);
    }

    private EvaluationRequest createEvaluationRequest(EvaluationRequestDto evaluationRequestDto) {
        AbstractClassifier classifier =
                classifierOptionsAdapter.convert(evaluationRequestDto.getClassifierOptions());
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setDataUuid(evaluationRequestDto.getTrainDataUuid());
        evaluationRequest.setClassifier(classifier);
        evaluationRequest.setEvaluationMethod(evaluationRequestDto.getEvaluationMethod());
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationRequestDto.getEvaluationMethod())) {
            evaluationRequest.setNumFolds(evaluationRequestDto.getNumFolds());
            evaluationRequest.setNumTests(evaluationRequestDto.getNumTests());
            evaluationRequest.setSeed(evaluationRequestDto.getSeed());
        }
        return evaluationRequest;
    }
}
