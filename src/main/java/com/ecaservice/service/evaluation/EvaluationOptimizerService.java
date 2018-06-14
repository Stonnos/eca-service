package com.ecaservice.service.evaluation;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.dto.evaluation.EvaluationMethod;
import com.ecaservice.dto.evaluation.EvaluationMethodReport;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.ClassifierReportMapper;
import com.ecaservice.mapping.EvaluationRequestMapper;
import com.ecaservice.model.InputData;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.service.ClassifierOptionsService;
import com.ecaservice.service.EvaluationResultsSender;
import com.ecaservice.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Implements classifier evaluation by searching optimal classifier options.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationOptimizerService {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final CrossValidationConfig crossValidationConfig;
    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationResultsSender evaluationResultsSender;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ClassifierReportMapper classifierReportMapper;
    private final EvaluationRequestMapper evaluationRequestMapper;
    private final ClassifierOptionsService classifierOptionsService;
    private final ErsRequestRepository ersRequestRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param crossValidationConfig               - cross - validation config bean
     * @param evaluationRequestService            - evaluation request service bean
     * @param evaluationResultsSender             - evaluation results sender bean
     * @param classifierOptionsRequestModelMapper - classifier options request model mapper bean
     * @param classifierReportMapper              - classifier report mapper bean
     * @param evaluationRequestMapper             - evaluation request mapper bean
     * @param classifierOptionsService            - classifier options service bean
     * @param ersRequestRepository                - ers request repository bean
     */
    @Inject
    public EvaluationOptimizerService(CrossValidationConfig crossValidationConfig,
                                      EvaluationRequestService evaluationRequestService,
                                      EvaluationResultsSender evaluationResultsSender,
                                      ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper,
                                      ClassifierReportMapper classifierReportMapper,
                                      EvaluationRequestMapper evaluationRequestMapper,
                                      ClassifierOptionsService classifierOptionsService,
                                      ErsRequestRepository ersRequestRepository) {
        this.crossValidationConfig = crossValidationConfig;
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationResultsSender = evaluationResultsSender;
        this.classifierOptionsRequestModelMapper = classifierOptionsRequestModelMapper;
        this.classifierReportMapper = classifierReportMapper;
        this.evaluationRequestMapper = evaluationRequestMapper;
        this.classifierOptionsService = classifierOptionsService;
        this.ersRequestRepository = ersRequestRepository;
    }

    /**
     * Evaluate model with optimal classifier options.
     *
     * @param data - training data
     * @return evaluation response
     */
    public EvaluationResponse evaluateWithOptimalClassifierOptions(Instances data) {
        ClassifierOptionsRequest classifierOptionsRequest = createClassifierOptionsRequest(data);
        ClassifierOptionsRequestModel requestModel = classifierOptionsRequestModelMapper.map(classifierOptionsRequest);
        requestModel.setRequestDate(LocalDateTime.now());
        requestModel.setDataMd5Hash(
                DigestUtils.md5DigestAsHex(classifierOptionsRequest.getInstances().getXmlInstances().getBytes()));
        ersRequestRepository.save(requestModel);
        try {
            ClassifierOptionsResponse response = evaluationResultsSender.getClassifierOptions(classifierOptionsRequest);
            requestModel.setRequestId(response.getRequestId());
            requestModel.setResponseStatus(response.getStatus());
            if (ResponseStatus.SUCCESS.equals(response.getStatus())) {
                requestModel.setClassifierOptionsResponseModels(
                        classifierReportMapper.map(response.getClassifierReports()));
                ClassifierReport classifierReport = response.getClassifierReports().stream().findFirst().orElse(null);
                if (Utils.validateClassifierOptions(classifierReport)) {
                    ClassifierOptions classifierOptions =
                            objectMapper.readValue(classifierReport.getOptions(), ClassifierOptions.class);
                    AbstractClassifier classifier = classifierOptionsService.convert(classifierOptions);
                    return evaluationRequestService.processRequest(
                            evaluationRequestMapper.map(classifierOptionsRequest, new InputData(classifier, data)));
                }
            }
        } catch (Exception ex) {
            log.error("There was an error while sending classifier options request: {}", ex.getMessage());
            requestModel.setResponseStatus(ResponseStatus.ERROR);
            requestModel.setDetails(ex.getMessage());
        } finally {
            ersRequestRepository.save(requestModel);
        }
        return Utils.buildErrorResponse(
                String.format("Can't find classifiers options for data '%s'", data.relationName()));
    }

    private ClassifierOptionsRequest createClassifierOptionsRequest(Instances data) {
        ClassifierOptionsRequest classifierOptionsRequest = new ClassifierOptionsRequest();
        classifierOptionsRequest.setInstances(Utils.buildInstancesReport(data));
        classifierOptionsRequest.setEvaluationMethodReport(new EvaluationMethodReport());
        classifierOptionsRequest.getEvaluationMethodReport().setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        classifierOptionsRequest.getEvaluationMethodReport().setNumFolds(
                BigInteger.valueOf(crossValidationConfig.getNumFolds()));
        classifierOptionsRequest.getEvaluationMethodReport().setNumTests(
                BigInteger.valueOf(crossValidationConfig.getNumTests()));
        classifierOptionsRequest.getEvaluationMethodReport().setSeed(
                BigInteger.valueOf(crossValidationConfig.getSeed()));
        return classifierOptionsRequest;
    }
}
