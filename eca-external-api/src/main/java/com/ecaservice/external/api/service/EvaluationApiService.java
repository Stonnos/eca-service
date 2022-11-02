package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.external.api.aspect.RequestExecution;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.dto.InstancesRequestDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import eca.core.evaluation.EvaluationMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.time.LocalDateTime;

/**
 * Evaluation API service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationApiService {

    private final EcaRequestMapper ecaRequestMapper;
    private final InstancesService instancesService;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final RabbitSender rabbitSender;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Processes evaluation request.
     *
     * @param ecaRequestEntity     - eca request entity
     * @param evaluationRequestDto - evaluation request dto.
     */
    @RequestExecution
    public void processRequest(EcaRequestEntity ecaRequestEntity, EvaluationRequestDto evaluationRequestDto) {
        log.info("Starting to process evaluation request [{}]", ecaRequestEntity.getCorrelationId());
        EvaluationRequest evaluationRequest = createEvaluationRequest(ecaRequestEntity, evaluationRequestDto);
        rabbitSender.sendEvaluationRequest(evaluationRequest, ecaRequestEntity.getCorrelationId());
        saveAsSent(ecaRequestEntity);
        log.info("Evaluation request [{}] has been sent to eca-server", ecaRequestEntity.getCorrelationId());
    }

    /**
     * Processes evaluation request using optimal classifier model.
     *
     * @param ecaRequestEntity    - eca request entity
     * @param instancesRequestDto - instances request dto.
     */
    @RequestExecution
    public void processRequest(EcaRequestEntity ecaRequestEntity, InstancesRequestDto instancesRequestDto) {
        log.info("Starting to process request [{}] for optimal classifier evaluation",
                ecaRequestEntity.getCorrelationId());
        Instances instances =
                loadInstances(instancesRequestDto.getTrainDataUrl(), ecaRequestEntity.getCorrelationId());
        InstancesRequest instancesRequest = new InstancesRequest();
        instancesRequest.setData(instances);
        rabbitSender.sendInstancesRequest(instancesRequest, ecaRequestEntity.getCorrelationId());
        saveAsSent(ecaRequestEntity);
        log.info("Optimal classifier evaluation request [{}] has been sent to eca-server",
                ecaRequestEntity.getCorrelationId());
    }

    /**
     * Processes experiment request.
     *
     * @param ecaRequestEntity     - eca request entity
     * @param experimentRequestDto - experiment request dto.
     */
    @RequestExecution
    public void processRequest(EcaRequestEntity ecaRequestEntity, ExperimentRequestDto experimentRequestDto) {
        log.info("Starting to process experiment request [{}]", ecaRequestEntity.getCorrelationId());
        Instances instances =
                loadInstances(experimentRequestDto.getTrainDataUrl(), ecaRequestEntity.getCorrelationId());
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setData(instances);
        experimentRequest.setEvaluationMethod(experimentRequestDto.getEvaluationMethod());
        experimentRequest.setExperimentType(ecaRequestMapper.map(experimentRequestDto.getExperimentType()));
        rabbitSender.sendExperimentRequest(experimentRequest, ecaRequestEntity.getCorrelationId());
        saveAsSent(ecaRequestEntity);
        log.info("Experiment request [{}] has been sent to eca-server", ecaRequestEntity.getCorrelationId());
    }

    private void saveAsSent(EcaRequestEntity ecaRequestEntity) {
        ecaRequestEntity.setRequestStage(RequestStageType.REQUEST_SENT);
        ecaRequestEntity.setRequestDate(LocalDateTime.now());
        ecaRequestRepository.save(ecaRequestEntity);
    }

    private EvaluationRequest createEvaluationRequest(EcaRequestEntity ecaRequestEntity,
                                                      EvaluationRequestDto evaluationRequestDto) {
        Instances instances =
                loadInstances(evaluationRequestDto.getTrainDataUrl(), ecaRequestEntity.getCorrelationId());
        AbstractClassifier classifier =
                classifierOptionsAdapter.convert(evaluationRequestDto.getClassifierOptions());
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setData(instances);
        evaluationRequest.setClassifier(classifier);
        evaluationRequest.setEvaluationMethod(evaluationRequestDto.getEvaluationMethod());
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationRequestDto.getEvaluationMethod())) {
            evaluationRequest.setNumFolds(evaluationRequestDto.getNumFolds());
            evaluationRequest.setNumTests(evaluationRequestDto.getNumTests());
            evaluationRequest.setSeed(evaluationRequestDto.getSeed());
        }
        return evaluationRequest;
    }

    private Instances loadInstances(String url, String correlationId) {
        log.info("Starting to load train data from [{}] for request [{}]", url, correlationId);
        Instances instances = instancesService.loadInstances(url);
        log.info("Train data has been loaded from [{}] for request [{}]", url, correlationId);
        return instances;

    }
}
