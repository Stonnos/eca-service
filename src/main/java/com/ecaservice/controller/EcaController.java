package com.ecaservice.controller;

import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.service.EvaluationLogService;
import com.ecaservice.service.EvaluationService;
import eca.model.InputData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implements the main rest controller for processing input requests.
 *
 * @author Roman Batygin
 */
@Slf4j
@RestController
@RequestMapping("/eca-service")
public class EcaController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");

    private EvaluationService evaluationService;
    private EvaluationLogService logService;

    /**
     * Constructor with dependency spring injection.
     *
     * @param evaluationService {@link EvaluationService} bean
     * @param logService        {@link EvaluationLogService} bean
     */
    @Autowired
    public EcaController(EvaluationService evaluationService, EvaluationLogService logService) {
        this.evaluationService = evaluationService;
        this.logService = logService;
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param model            input options
     * @param evaluationMethod evaluation method
     * @param numFolds         the number of folds for k * V cross - validation method
     * @param numTests         the number of tests for k * V cross - validation method
     * @return <tt>ResponseEntity</tt> object
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public ResponseEntity<ByteArrayResource>
    execute(@RequestPart(value = "model") ByteArrayResource model,
            @RequestParam(value = "evaluationMethod") String evaluationMethod,
            @RequestParam(value = "numFolds", required = false) Integer numFolds,
            @RequestParam(value = "numTests", required = false) Integer numTests,
            HttpServletRequest request) {

        LocalDateTime requestDate = LocalDateTime.now();
        String ipAddress = request.getRemoteAddr();

        log.info("Received request for client {} at: {}", ipAddress, DATE_FORMAT.format(requestDate));

        log.info("Starting to read input data.");

        InputData inputData = (InputData) SerializationUtils.deserialize(model.getByteArray());

        log.info("Input data has been successfully read!");

        EvaluationMethod method = EvaluationMethod.valueOf(evaluationMethod);

        ClassificationResult result = evaluationService.evaluateModel(inputData.getClassifier(),
                inputData.getData(), method, numFolds, numTests);

        logService.save(result, method, numFolds, numTests, requestDate, ipAddress);

        if (result.isSuccess()) {
            log.info("Starting classification results serialization.");

            byte[] bytes = SerializationUtils.serialize(result.getClassifierDescriptor());

            log.info("Classification results has been successfully serialized!");

            return new ResponseEntity<>(new ByteArrayResource(bytes), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
