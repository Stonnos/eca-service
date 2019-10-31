package com.ecaservice.controller.web;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.mapping.EvaluationResultsMapper;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.conversion.ClassifierOptionsConverter;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
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

    private static final String ATTACHMENT_FORMAT = "attachment;filename=%s%s.xml";

    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final ClassifierOptionsConverter classifierOptionsConverter;
    private final ExperimentRequestService experimentRequestService;
    private final EvaluationOptimizerService evaluationOptimizerService;
    private final Jaxb2Marshaller ersMarshaller;

    /**
     * Constructor with spring dependency injection.
     *
     * @param evaluationRequestService   - evaluation request service bean
     * @param evaluationResultsMapper    - evaluation results mapper bean
     * @param classifierOptionsConverter   - classifier options service bean
     * @param experimentRequestService   - experiment request service bean
     * @param evaluationOptimizerService - evaluation optimizer service bean
     * @param ersMarshaller              - jaxb2 marshaller bean
     */
    @Inject
    public QaController(EvaluationRequestService evaluationRequestService,
                        EvaluationResultsMapper evaluationResultsMapper,
                        ClassifierOptionsConverter classifierOptionsConverter,
                        ExperimentRequestService experimentRequestService,
                        EvaluationOptimizerService evaluationOptimizerService,
                        Jaxb2Marshaller ersMarshaller) {
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationResultsMapper = evaluationResultsMapper;
        this.classifierOptionsConverter = classifierOptionsConverter;
        this.experimentRequestService = experimentRequestService;
        this.evaluationOptimizerService = evaluationOptimizerService;
        this.ersMarshaller = ersMarshaller;
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param trainingData        - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param classifierOptions   - classifier options json file
     * @param evaluationMethod    - evaluation method
     * @param httpServletResponse - http servlet response
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Evaluates classifier with specified options",
            notes = "Evaluates classifier with specified options"
    )
    @PostMapping(value = "/evaluate")
    public void evaluate(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @ApiParam(value = "Classifier input options json file", required = true) @RequestParam
                    MultipartFile classifierOptions,
            @ApiParam(value = "Evaluation method", required = true) @RequestParam EvaluationMethod evaluationMethod,
            HttpServletResponse httpServletResponse) throws Exception {
        log.info("Received evaluation request for data {}, classifier options [{}], evaluation method [{}]",
                trainingData.getOriginalFilename(), classifierOptions.getOriginalFilename(), evaluationMethod);
        EvaluationRequest evaluationRequest =
                createEvaluationRequest(trainingData, classifierOptions, evaluationMethod);
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequest);
        processResponse(evaluationResponse, httpServletResponse);
    }

    /**
     * Creates experiment request.
     *
     * @param trainingData     - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param firstName        - first name
     * @param email            - email
     * @param experimentType   - experiment type
     * @param evaluationMethod - evaluation method
     * @return experiment uuid
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Creates experiment request with specified options",
            notes = "Creates experiment request with specified options"
    )
    @PostMapping(value = "/experiment")
    public String createExperiment(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @ApiParam(value = "Request first name", required = true) @RequestParam String firstName,
            @ApiParam(value = "Email", required = true) @RequestParam String email,
            @ApiParam(value = "Experiment type", required = true) @RequestParam ExperimentType experimentType,
            @ApiParam(value = "Evaluation method", required = true) @RequestParam EvaluationMethod evaluationMethod)
            throws Exception {
        log.info("Received experiment request for data '{}', email '{}'", trainingData.getOriginalFilename(), email);
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setFirstName(firstName);
        experimentRequest.setEmail(email);
        experimentRequest.setData(loadInstances(trainingData));
        experimentRequest.setExperimentType(experimentType);
        experimentRequest.setEvaluationMethod(evaluationMethod);
        Experiment experiment = experimentRequestService.createExperimentRequest(experimentRequest);
        return experiment.getUuid();
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param trainingData        - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param httpServletResponse - http servlet response
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Evaluates classifier using optimal options",
            notes = "Evaluates classifier using optimal options"
    )
    @PostMapping(value = "/optimize")
    public void optimize(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            HttpServletResponse httpServletResponse) throws Exception {
        log.info("Received optimization request for data {}", trainingData.getOriginalFilename());
        Instances instances = loadInstances(trainingData);
        EvaluationResponse evaluationResponse =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(new InstancesRequest(instances));
        processResponse(evaluationResponse, httpServletResponse);
    }

    private EvaluationRequest createEvaluationRequest(MultipartFile trainingData, MultipartFile classifierOptions,
                                                      EvaluationMethod evaluationMethod) throws Exception {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setData(loadInstances(trainingData));
        evaluationRequest.setEvaluationMethod(evaluationMethod);
        evaluationRequest.setEvaluationOptionsMap(Collections.emptyMap());
        ClassifierOptions options = parseOptions(classifierOptions.getInputStream());
        AbstractClassifier classifier = classifierOptionsConverter.convert(options);
        evaluationRequest.setClassifier(classifier);
        return evaluationRequest;
    }

    private Instances loadInstances(MultipartFile trainingData) throws Exception {
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MultipartFileResource(trainingData));
        return fileDataLoader.loadInstances();
    }

    private void writeXmlResult(HttpServletResponse response, Object xmlObject) throws Exception {
        try (OutputStream outputStream = response.getOutputStream()) {
            ersMarshaller.marshal(xmlObject, new StreamResult(outputStream));
        }
    }

    private void processResponse(EvaluationResponse evaluationResponse, HttpServletResponse response) throws Exception {
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
        if (!TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
            throw new IllegalStateException(evaluationResponse.getErrorMessage());
        } else {
            EvaluationResultsRequest evaluationResultsRequest =
                    evaluationResultsMapper.map(evaluationResponse.getEvaluationResults());
            response.setContentType(MediaType.APPLICATION_XML_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT,
                    evaluationResponse.getEvaluationResults().getClassifier().getClass().getSimpleName(),
                    evaluationResponse.getRequestId()));
            writeXmlResult(response, evaluationResultsRequest);
        }
    }
}
