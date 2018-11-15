package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.mapping.EvaluationResultsMapper;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.service.ClassifierOptionsService;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.StringReader;
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

    private static final String ATTACHMENT_FORMAT = "attachment;filename=%s_%d.xml";

    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final ClassifierOptionsService classifierOptionsService;
    private final ExperimentRequestService experimentRequestService;
    private final EvaluationOptimizerService evaluationOptimizerService;
    private final Jaxb2Marshaller ersMarshaller;

    /**
     * Constructor with spring dependency injection.
     *
     * @param evaluationRequestService   - evaluation request service bean
     * @param evaluationResultsMapper    - evaluation results mapper bean
     * @param classifierOptionsService   - classifier options service bean
     * @param experimentRequestService   - experiment request service bean
     * @param evaluationOptimizerService - evaluation optimizer service bean
     * @param ersMarshaller              - jaxb2 marshaller bean
     */
    @Inject
    public QaController(EvaluationRequestService evaluationRequestService,
                        EvaluationResultsMapper evaluationResultsMapper,
                        ClassifierOptionsService classifierOptionsService,
                        ExperimentRequestService experimentRequestService,
                        EvaluationOptimizerService evaluationOptimizerService,
                        Jaxb2Marshaller ersMarshaller) {
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationResultsMapper = evaluationResultsMapper;
        this.classifierOptionsService = classifierOptionsService;
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
    @ApiOperation(
            value = "Evaluates classifier with specified options",
            notes = "Evaluates classifier with specified options"
    )
    @PostMapping(value = "/evaluate")
    public void evaluate(@RequestParam MultipartFile trainingData,
                         @RequestParam MultipartFile classifierOptions,
                         @RequestParam EvaluationMethod evaluationMethod,
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
                                           @RequestParam EvaluationMethod evaluationMethod) throws Exception {
        log.info("Received experiment request for data '{}', email '{}'", trainingData.getOriginalFilename(), email);
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setFirstName(firstName);
        experimentRequest.setEmail(email);
        experimentRequest.setData(loadInstances(trainingData));
        experimentRequest.setExperimentType(experimentType);
        experimentRequest.setEvaluationMethod(evaluationMethod);
        EcaResponse ecaResponse = experimentRequestService.createExperimentRequest(experimentRequest);
        return ResponseEntity.ok(ecaResponse);
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param trainingData        - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param httpServletResponse - http servlet response
     */
    @ApiOperation(
            value = "Evaluates classifier using optimal options",
            notes = "Evaluates classifier using optimal options"
    )
    @PostMapping(value = "/optimize")
    public void optimize(@RequestParam MultipartFile trainingData, HttpServletResponse httpServletResponse)
            throws Exception {
        log.info("Received optimization request for data {}", trainingData.getOriginalFilename());
        Instances instances = loadInstances(trainingData);
        EvaluationResponse evaluationResponse =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(new InstancesRequest(instances));
        processResponse(evaluationResponse, httpServletResponse);
    }

    /**
     * Handles error.
     *
     * @param ex - exception
     * @return response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleError(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    private EvaluationRequest createEvaluationRequest(MultipartFile trainingData, MultipartFile classifierOptions,
                                                      EvaluationMethod evaluationMethod) throws Exception {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setData(loadInstances(trainingData));
        evaluationRequest.setEvaluationMethod(evaluationMethod);
        evaluationRequest.setEvaluationOptionsMap(Collections.emptyMap());
        ClassifierOptions options = parseOptions(classifierOptions.getInputStream());
        AbstractClassifier classifier = classifierOptionsService.convert(options);
        evaluationRequest.setClassifier(classifier);
        return evaluationRequest;
    }

    private Instances loadInstances(MultipartFile trainingData) throws Exception {
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MultipartFileResource(trainingData));
        return fileDataLoader.loadInstances();
    }

    private void writeXmlResult(HttpServletResponse response, String xml) throws Exception {
        try (StringReader reader = new StringReader(xml); OutputStream outputStream = response.getOutputStream()) {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(reader));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        }
    }

    private void processResponse(EvaluationResponse evaluationResponse, HttpServletResponse response) throws Exception {
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
        if (TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
            EvaluationResultsRequest evaluationResultsRequest =
                    evaluationResultsMapper.map(evaluationResponse.getEvaluationResults());
            StringResult stringResult = new StringResult();
            ersMarshaller.marshal(evaluationResultsRequest, stringResult);
            response.setContentType(MediaType.APPLICATION_XML_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT,
                    evaluationResponse.getEvaluationResults().getClassifier().getClass().getSimpleName(),
                    System.currentTimeMillis()));
            writeXmlResult(response, stringResult.toString());
        }
    }
}
