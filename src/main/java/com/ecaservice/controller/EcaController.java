package com.ecaservice.controller;

import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.service.EcaService;
import eca.beans.InputData;
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

/**
 * Implements the main rest controller which
 * processed input requests.
 * @author Roman Batygin
 */
@Slf4j
@RestController
@RequestMapping("/eca-service")
public class EcaController {

    private EcaService ecaService;

    @Autowired
    public EcaController(EcaService ecaService) {
        this.ecaService = ecaService;
    }

    /**
     * Processed the request on classifier model evaluation.
     * @param model input options
     * @param evaluationMethod evaluation method
     * @param numFolds the number of folds for k*V cross - validation method
     * @param numTests the number of tests for k*V cross - validation method
     * @return <tt>ResponseEntity</tt> object
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public ResponseEntity<ByteArrayResource>
                execute(@RequestPart(value = "model") ByteArrayResource model,
                        @RequestParam(value = "evaluationMethod") String evaluationMethod,
                        @RequestParam(value = "numFolds", required = false) Integer numFolds,
                        @RequestParam(value = "numTests", required = false) Integer numTests) {

        log.info("Reading input data");

        InputData inputData = (InputData) SerializationUtils.deserialize(model.getByteArray());

        log.info("Input input has been successfully read!");

        ClassificationResult result = ecaService.execute(inputData.getClassifier(),
                inputData.getData(), EvaluationMethod.valueOf(evaluationMethod),
                numFolds, numTests);

        if (result.isSuccess()) {
            log.info("Starting classification results serialization");

            byte[] bytes = SerializationUtils.serialize(result.getClassifierDescriptor());

            log.info("Classification results has been successfully serialized");

            return new ResponseEntity<>(new ByteArrayResource(bytes), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
