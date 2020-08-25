package com.ecaservice.controller.qa;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import com.ecaservice.service.evaluation.EvaluationResultsService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;

import static com.ecaservice.util.ClassifierOptionsHelper.parseOptions;

/**
 * QA controller.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for QA")
@Slf4j
@RestController
@RequestMapping("/qa")
@Profile("!docker-prod")
@RequiredArgsConstructor
public class QaController {

    private static final String ATTACHMENT_FORMAT = "attachment;filename=%s%s.xml";

    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationResultsService evaluationResultsService;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final ExperimentRequestService experimentRequestService;
    private final EvaluationOptimizerService evaluationOptimizerService;
    private final Jaxb2Marshaller ersMarshaller;

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
     * @return experiment request id
     */
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
        return experiment.getRequestId();
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
        @Cleanup InputStream inputStream = classifierOptions.getInputStream();
        ClassifierOptions options = parseOptions(inputStream);
        AbstractClassifier classifier = classifierOptionsAdapter.convert(options);
        evaluationRequest.setClassifier(classifier);
        return evaluationRequest;
    }

    private Instances loadInstances(MultipartFile trainingData) throws Exception {
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MultipartFileResource(trainingData));
        return fileDataLoader.loadInstances();
    }

    private void writeXmlResult(HttpServletResponse response, Object xmlObject) throws Exception {
        @Cleanup OutputStream outputStream = response.getOutputStream();
        ersMarshaller.marshal(xmlObject, new StreamResult(outputStream));
    }

    private void processResponse(EvaluationResponse evaluationResponse, HttpServletResponse response) throws Exception {
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
        if (!TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
            throw new IllegalStateException(evaluationResponse.getErrorMessage());
        } else {
            EvaluationResultsRequest evaluationResultsRequest =
                    evaluationResultsService.proceed(evaluationResponse.getEvaluationResults());
            response.setContentType(MediaType.APPLICATION_XML_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT,
                    evaluationResponse.getEvaluationResults().getClassifier().getClass().getSimpleName(),
                    evaluationResponse.getRequestId()));
            writeXmlResult(response, evaluationResultsRequest);
        }
    }
}
