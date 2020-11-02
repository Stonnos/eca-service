package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.external.api.aspect.ErrorExecution;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.UrlResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.net.URL;

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

    /**
     * Processes evaluation request.
     *
     * @param correlationId        - correlation id
     * @param evaluationRequestDto - evaluation request dto.
     */
    @ErrorExecution
    public void processRequest(String correlationId, EvaluationRequestDto evaluationRequestDto) {
        EvaluationRequest evaluationRequest = createEvaluationRequest(evaluationRequestDto);
        rabbitSender.sendEvaluationRequest(evaluationRequest, correlationId);
    }

    private EvaluationRequest createEvaluationRequest(EvaluationRequestDto evaluationRequestDto) {
        try {
            UrlResource urlResource = new UrlResource(new URL(evaluationRequestDto.getTrainDataUrl()));
            fileDataLoader.setSource(urlResource);
            Instances instances = fileDataLoader.loadInstances();
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
