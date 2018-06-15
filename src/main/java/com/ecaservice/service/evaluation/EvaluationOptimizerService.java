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
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.service.ClassifierOptionsService;
import com.ecaservice.service.EvaluationResultsSender;
import com.ecaservice.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Implements classifier evaluation by searching optimal classifier options.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationOptimizerService {

    private static final String RESULTS_NOT_FOUND_MESSAGE = "Can't find classifiers options for data '%s'";

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final CrossValidationConfig crossValidationConfig;
    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationResultsSender evaluationResultsSender;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ClassifierReportMapper classifierReportMapper;
    private final EvaluationRequestMapper evaluationRequestMapper;
    private final ClassifierOptionsService classifierOptionsService;
    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param crossValidationConfig                   - cross - validation config bean
     * @param evaluationRequestService                - evaluation request service bean
     * @param evaluationResultsSender                 - evaluation results sender bean
     * @param classifierOptionsRequestModelMapper     - classifier options request model mapper bean
     * @param classifierReportMapper                  - classifier report mapper bean
     * @param evaluationRequestMapper                 - evaluation request mapper bean
     * @param classifierOptionsService                - classifier options service bean
     * @param classifierOptionsRequestModelRepository - classifier options request model repository bean
     */
    @Inject
    public EvaluationOptimizerService(CrossValidationConfig crossValidationConfig,
                                      EvaluationRequestService evaluationRequestService,
                                      EvaluationResultsSender evaluationResultsSender,
                                      ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper,
                                      ClassifierReportMapper classifierReportMapper,
                                      EvaluationRequestMapper evaluationRequestMapper,
                                      ClassifierOptionsService classifierOptionsService,
                                      ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository) {
        this.crossValidationConfig = crossValidationConfig;
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationResultsSender = evaluationResultsSender;
        this.classifierOptionsRequestModelMapper = classifierOptionsRequestModelMapper;
        this.classifierReportMapper = classifierReportMapper;
        this.evaluationRequestMapper = evaluationRequestMapper;
        this.classifierOptionsService = classifierOptionsService;
        this.classifierOptionsRequestModelRepository = classifierOptionsRequestModelRepository;
    }

    /**
     * Evaluate model with optimal classifier options.
     *
     * @param data - training data
     * @return evaluation response
     */
    public EvaluationResponse evaluateWithOptimalClassifierOptions(Instances data) {
        ClassifierOptionsRequest classifierOptionsRequest = createClassifierOptionsRequest(data);
        String dataMd5Hash =
                DigestUtils.md5DigestAsHex(classifierOptionsRequest.getInstances().getXmlInstances().getBytes());
        ClassifierOptionsResponseModel responseModel = findLastClassifierOptionsResponseModel(dataMd5Hash);
        if (Utils.validateClassifierOptions(responseModel)) {
            return evaluateModel(classifierOptionsRequest, responseModel.getOptions(), data);
        } else {
            ClassifierOptionsRequestModel requestModel =
                    classifierOptionsRequestModelMapper.map(classifierOptionsRequest);
            requestModel.setRequestDate(LocalDateTime.now());
            requestModel.setDataMd5Hash(dataMd5Hash);
            classifierOptionsRequestModelRepository.save(requestModel);
            try {
                ClassifierOptionsResponse response =
                        evaluationResultsSender.getClassifierOptions(classifierOptionsRequest);
                requestModel.setRequestId(response.getRequestId());
                requestModel.setResponseStatus(response.getStatus());
                if (ResponseStatus.SUCCESS.equals(response.getStatus())) {
                    ClassifierReport classifierReport =
                            response.getClassifierReports().stream().findFirst().orElse(null);
                    if (Utils.validateClassifierOptions(classifierReport)) {
                        requestModel.setClassifierOptionsResponseModels(
                                Collections.singletonList(classifierReportMapper.map(classifierReport)));
                        return evaluateModel(classifierOptionsRequest, classifierReport.getOptions(), data);
                    }
                }
            } catch (Exception ex) {
                log.error("There was an error while sending classifier options request: {}", ex.getMessage());
                requestModel.setResponseStatus(ResponseStatus.ERROR);
                requestModel.setDetails(ex.getMessage());
            } finally {
                classifierOptionsRequestModelRepository.save(requestModel);
            }
        }
        return Utils.buildErrorResponse(String.format(RESULTS_NOT_FOUND_MESSAGE, data.relationName()));
    }

    private ClassifierOptionsResponseModel findLastClassifierOptionsResponseModel(String dataMd5Hash) {
        List<ClassifierOptionsRequestModel> requestModels =
                classifierOptionsRequestModelRepository.findLastRequests(dataMd5Hash,
                        Collections.singleton(ResponseStatus.SUCCESS),
                        LocalDateTime.now().minusDays(crossValidationConfig.getClassifierOptionsCacheDurationInDays()),
                        new PageRequest(0, 1));
        return requestModels.stream().filter(requestModel -> !CollectionUtils.isEmpty(requestModel
                .getClassifierOptionsResponseModels())).findFirst().map(requestModel -> requestModel
                .getClassifierOptionsResponseModels().stream().findFirst().orElse(null)).orElse(null);
    }

    private EvaluationResponse evaluateModel(ClassifierOptionsRequest classifierOptionsRequest, String options,
                                             Instances data) {
        try {
            ClassifierOptions classifierOptions = objectMapper.readValue(options, ClassifierOptions.class);
            AbstractClassifier classifier = classifierOptionsService.convert(classifierOptions);
            return evaluationRequestService.processRequest(
                    evaluationRequestMapper.map(classifierOptionsRequest, new InputData(classifier, data)));
        } catch (Exception ex) {
            log.error("There was an error: {}", ex.getMessage());
        }
        return Utils.buildErrorResponse(String.format(RESULTS_NOT_FOUND_MESSAGE, data.relationName()));
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
