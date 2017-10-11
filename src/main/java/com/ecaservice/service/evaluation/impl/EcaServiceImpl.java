package com.ecaservice.service.evaluation.impl;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.mapping.OrikaBeanMapper;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationRequest;
import com.ecaservice.model.evaluation.EvaluationStatus;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.EcaService;
import com.ecaservice.service.evaluation.EvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EcaServiceImpl implements EcaService {

    private final CrossValidationConfig crossValidationConfig;
    private final CalculationExecutorService executorService;
    private final EvaluationService evaluationService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final OrikaBeanMapper mapper;

    /**
     * Constructor with dependency spring injection.
     *
     * @param crossValidationConfig   {@link CrossValidationConfig} bean
     * @param executorService         {@link java.util.concurrent.ExecutorService} bean
     * @param evaluationService       {@link EvaluationService} bean
     * @param evaluationLogRepository {@link EvaluationLogRepository} bean
     * @param mapper                  {@link OrikaBeanMapper} bean
     */
    @Autowired
    public EcaServiceImpl(CrossValidationConfig crossValidationConfig,
                          CalculationExecutorService executorService,
                          EvaluationService evaluationService,
                          EvaluationLogRepository evaluationLogRepository, OrikaBeanMapper mapper) {
        this.crossValidationConfig = crossValidationConfig;
        this.executorService = executorService;
        this.evaluationService = evaluationService;
        this.evaluationLogRepository = evaluationLogRepository;
        this.mapper = mapper;
    }

    @Override
    public EvaluationResponse processRequest(final EvaluationRequest request) {

        Assert.notNull(request, "Evaluation request is not specified!");

        EvaluationLog evaluationLog = mapper.map(request, EvaluationLog.class);
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
                evaluationLog.setEvaluationStatus(EvaluationStatus.ERROR);
                evaluationLog.setErrorMessage(classificationResult.getErrorMessage());
                evaluationResponse.setStatus(TechnicalStatus.ERROR);
                evaluationResponse.setErrorMessage(classificationResult.getErrorMessage());
            }
        } catch (TimeoutException ex) {
            log.warn("There was a timeout.");
            evaluationLog.setEvaluationStatus(EvaluationStatus.TIMEOUT);
            evaluationResponse.setStatus(TechnicalStatus.TIMEOUT);
        } catch (Throwable ex) {
            log.error("There was an error occurred in evaluation : {}", ex);
            evaluationLog.setEvaluationStatus(EvaluationStatus.ERROR);
            evaluationLog.setErrorMessage(ex.getMessage());
            evaluationResponse.setStatus(TechnicalStatus.ERROR);
            evaluationResponse.setErrorMessage(ex.getMessage());
        } finally {
            evaluationLog.setEndDate(LocalDateTime.now());
            evaluationLogRepository.save(evaluationLog);
        }

        return evaluationResponse;
    }
}
