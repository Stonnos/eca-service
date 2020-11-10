package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.external.api.aspect.ErrorExecution;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.UrlResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.net.URL;
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

    private final FileDataLoader fileDataLoader;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final RabbitSender rabbitSender;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Processes evaluation request.
     *
     * @param ecaRequestEntity     - eca request entity
     * @param evaluationRequestDto - evaluation request dto.
     */
    @ErrorExecution
    public void processRequest(EcaRequestEntity ecaRequestEntity, EvaluationRequestDto evaluationRequestDto) {
        EvaluationRequest evaluationRequest = createEvaluationRequest(ecaRequestEntity, evaluationRequestDto);
        rabbitSender.sendEvaluationRequest(evaluationRequest, ecaRequestEntity.getCorrelationId());
        ecaRequestEntity.setRequestStage(RequestStageType.REQUEST_SENT);
        ecaRequestEntity.setRequestDate(LocalDateTime.now());
        ecaRequestRepository.save(ecaRequestEntity);
    }

    private EvaluationRequest createEvaluationRequest(EcaRequestEntity ecaRequestEntity,
                                                      EvaluationRequestDto evaluationRequestDto) {
        try {
            log.debug("Starting to load train data from {} for request [{}]", evaluationRequestDto.getTrainDataUrl(),
                    ecaRequestEntity.getCorrelationId());
            UrlResource urlResource = new UrlResource(new URL(evaluationRequestDto.getTrainDataUrl()));
            fileDataLoader.setSource(urlResource);
            Instances instances = fileDataLoader.loadInstances();
            log.debug("Train data has been loaded from {} for request [{}]", evaluationRequestDto.getTrainDataUrl(),
                    ecaRequestEntity.getCorrelationId());
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
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
