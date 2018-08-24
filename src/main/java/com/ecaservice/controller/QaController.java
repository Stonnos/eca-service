package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.mapping.EvaluationResultsMapper;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.service.ClassifierOptionsService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import eca.data.file.FileDataLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.util.Utils.parseOptions;

/**
 * QA controller.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for QA")
@Slf4j
@RestController
@RequestMapping("/qa")
public class QaController {

    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final ClassifierOptionsService classifierOptionsService;
    private final ExperimentRequestService experimentRequestService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param evaluationRequestService - evaluation request service bean
     * @param evaluationResultsMapper  - evaluation results mapper bean
     * @param classifierOptionsService - classifier options service bean
     * @param experimentRequestService - experiment request service bean
     */
    @Inject
    public QaController(EvaluationRequestService evaluationRequestService,
                        EvaluationResultsMapper evaluationResultsMapper,
                        ClassifierOptionsService classifierOptionsService,
                        ExperimentRequestService experimentRequestService) {
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationResultsMapper = evaluationResultsMapper;
        this.classifierOptionsService = classifierOptionsService;
        this.experimentRequestService = experimentRequestService;
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param trainingData      - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param classifierOptions - classifier options as json string
     * @param evaluationMethod  - evaluation method
     * @return evaluation results xml response
     */
    @ApiOperation(
            value = "Evaluates classifier with specified options",
            notes = "Evaluates classifier with specified options"
    )
    @PostMapping(value = "/evaluate", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity evaluate(@RequestParam MultipartFile trainingData,
                                   @RequestParam String classifierOptions,
                                   @RequestParam EvaluationMethod evaluationMethod) {
        log.info("Received evaluation request for data {}, classifier options [{}], evaluation method [{}]",
                trainingData.getOriginalFilename(), classifierOptions, evaluationMethod);
        try {
            EvaluationRequest evaluationRequest =
                    createEvaluationRequest(trainingData, classifierOptions, evaluationMethod);
            EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequest);
            if (TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
                EvaluationResultsRequest evaluationResultsRequest =
                        evaluationResultsMapper.map(evaluationResponse.getEvaluationResults());
                return ResponseEntity.ok(evaluationResultsRequest);
            }
            return ResponseEntity.ok(evaluationResponse.getErrorMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Creates experiment request.
     *
     * @param trainingData     - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param firstName        - first name
     * @param email            - email
     * @param experimentType   - experiment type
     * @param evaluationMethod - evaluation method
     * @return response entity
     */
    @ApiOperation(
            value = "Creates experiment request with specified options",
            notes = "Creates experiment request with specified options"
    )
    @PostMapping(value = "/experiment")
    public ResponseEntity createExperiment(@RequestParam MultipartFile trainingData,
                                           @RequestParam String firstName,
                                           @RequestParam String email,
                                           @RequestParam ExperimentType experimentType,
                                           @RequestParam EvaluationMethod evaluationMethod) {
        log.info("Received experiment request for data '{}', email '{}'", trainingData.getOriginalFilename(), email);
        try {
            ExperimentRequest experimentRequest = new ExperimentRequest();
            experimentRequest.setFirstName(firstName);
            experimentRequest.setEmail(email);
            experimentRequest.setData(loadInstances(trainingData));
            experimentRequest.setExperimentType(experimentType);
            experimentRequest.setEvaluationMethod(evaluationMethod);
            EcaResponse ecaResponse = experimentRequestService.createExperimentRequest(experimentRequest);
            return ResponseEntity.ok(ecaResponse);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    private EvaluationRequest createEvaluationRequest(MultipartFile trainingData, String classifierOptions,
                                                      EvaluationMethod evaluationMethod) throws Exception {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setData(loadInstances(trainingData));
        evaluationRequest.setEvaluationMethod(evaluationMethod);
        evaluationRequest.setEvaluationOptionsMap(Collections.emptyMap());
        ClassifierOptions options = parseOptions(classifierOptions);
        AbstractClassifier classifier = classifierOptionsService.convert(options);
        evaluationRequest.setClassifier(classifier);
        return evaluationRequest;
    }

    private Instances loadInstances(MultipartFile trainingData) throws Exception {
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MultipartFileResource(trainingData));
        return fileDataLoader.loadInstances();
    }
}
