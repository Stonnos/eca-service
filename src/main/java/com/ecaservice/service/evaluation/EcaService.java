package com.ecaservice.service.evaluation;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationRequest;
import com.ecaservice.model.evaluation.EvaluationStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Eca - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EcaService {

    private final CrossValidationConfig crossValidationConfig;
    private final CalculationExecutorService executorService;
    private final EvaluationService evaluationService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationLogMapper evaluationLogMapper;

    /**
     * Constructor with dependency spring injection.
     *
     * @param crossValidationConfig   {@link CrossValidationConfig} bean
     * @param executorService         {@link java.util.concurrent.ExecutorService} bean
     * @param evaluationService       {@link EvaluationService} bean
     * @param evaluationLogRepository {@link EvaluationLogRepository} bean
     * @param evaluationLogMapper     {@link EvaluationLogMapper} bean
     */
    @Autowired
    public EcaService(CrossValidationConfig crossValidationConfig,
                      CalculationExecutorService executorService,
                      EvaluationService evaluationService,
                      EvaluationLogRepository evaluationLogRepository,
                      EvaluationLogMapper evaluationLogMapper) {
        this.crossValidationConfig = crossValidationConfig;
        this.executorService = executorService;
        this.evaluationService = evaluationService;
        this.evaluationLogRepository = evaluationLogRepository;
        this.evaluationLogMapper = evaluationLogMapper;
    }

    /**
     * Processes input request and returns classification results.
     *
     * @param request {@link EvaluationRequest} object
     * @return {@link EvaluationResponse} object
     */
    public EvaluationResponse processRequest(final EvaluationRequest request) {

        Assert.notNull(request, "Evaluation request is not specified!");

        EvaluationLog evaluationLog = evaluationLogMapper.map(request);
        evaluationLog.setEvaluationStatus(EvaluationStatus.NEW);
        evaluationLog.setCreationDate(LocalDateTime.now());
        evaluationLog.setStartDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);

        EvaluationResponse evaluationResponse = new EvaluationResponse();

        try {
            Callable<ClassificationResult> callable = () ->
                    evaluationService.evaluateModel(request.getInputData(), request.getEvaluationMethod(),
                            request.getEvaluationOptionsMap());

            ClassificationResult classificationResult = executorService.execute(callable,
                    crossValidationConfig.getTimeout(), TimeUnit.MINUTES);

            if (classificationResult.isSuccess()) {
                evaluationLog.setEvaluationStatus(EvaluationStatus.FINISHED);
                evaluationResponse.setEvaluationResults(classificationResult.getEvaluationResults());
                evaluationResponse.setStatus(TechnicalStatus.SUCCESS);
            } else {
                handleError(evaluationLog, evaluationResponse, classificationResult.getErrorMessage());
            }
        } catch (TimeoutException ex) {
            log.warn("There was a timeout for evaluation with id = {}.", evaluationLog.getId());
            evaluationLog.setEvaluationStatus(EvaluationStatus.TIMEOUT);
            evaluationResponse.setStatus(TechnicalStatus.TIMEOUT);
        } catch (Exception ex) {
            log.error("There was an error occurred for evaluation with id = {}: {}", evaluationLog.getId(), ex);
            handleError(evaluationLog, evaluationResponse, ex.getMessage());
        } finally {
            evaluationLog.setEndDate(LocalDateTime.now());
            evaluationLogRepository.save(evaluationLog);
        }

        return evaluationResponse;
    }

    private void handleError(EvaluationLog evaluationLog, EvaluationResponse evaluationResponse, String errorMessage) {
        evaluationLog.setEvaluationStatus(EvaluationStatus.ERROR);
        evaluationLog.setErrorMessage(errorMessage);
        evaluationResponse.setStatus(TechnicalStatus.ERROR);
        evaluationResponse.setErrorMessage(errorMessage);
    }
}
