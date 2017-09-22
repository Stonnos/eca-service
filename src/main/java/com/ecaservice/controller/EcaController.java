package com.ecaservice.controller;

import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.service.EcaService;
import com.ecaservice.model.InputData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements the main rest controller for processing input requests.
 *
 * @author Roman Batygin
 */
@Slf4j
@RestController
@RequestMapping("/eca-service")
public class EcaController {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

    private final EcaService ecaService;

    /**
     * Constructor with dependency spring injection.
     *
     * @param ecaService {@link EcaService} bean
     */
    @Autowired
    public EcaController(EcaService ecaService) {
        this.ecaService = ecaService;
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param model            input options
     * @param evaluationMethod evaluation method
     * @param numFolds         the number of folds for k * V cross - validation method
     * @param numTests         the number of tests for k * V cross - validation method
     * @return {@link ResponseEntity} object
     */
    /*
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public ResponseEntity<ClassificationResultsDto>
    execute(@RequestPart(value = "model") ByteArrayResource model,
            @RequestParam(value = "evaluationMethod") String evaluationMethod,
            @RequestParam(value = "numFolds", required = false) Integer numFolds,
            @RequestParam(value = "numTests", required = false) Integer numTests,
            HttpServletRequest request) {

        Date requestDate = new Date();
        String ipAddress = request.getRemoteAddr();

        log.info("Received request for client {} at: {}", ipAddress, DATE_FORMAT.format(requestDate));

        log.info("Starting to read input data.");

        InputData inputData = (InputData) SerializationUtils.deserialize(model.getByteArray());

        log.info("Input data has been successfully read!");

        EvaluationMethod method = EvaluationMethod.valueOf(evaluationMethod);

        EvaluationRequest evaluationRequest = new EvaluationRequest(ipAddress, requestDate,
                inputData, method, numFolds, numTests);

        ClassificationResult result = ecaService.processRequest(evaluationRequest);


        if (result != null && result.isSuccess()) {
            log.info("Starting classification results serialization.");

            byte[] bytes = SerializationUtils.serialize(result.getClassifierDescriptor());

            log.info("Classification results has been successfully serialized!");

            return new ResponseEntity<>(new ByteArrayResource(bytes), HttpStatus.OK);
        }

        ClassificationResultsDto classificationResultsDto = new ClassificationResultsDto();
        classificationResultsDto.setClassifierDescriptor(result.getClassifierDescriptor());
        classificationResultsDto.setSuccess(result.isSuccess());
        classificationResultsDto.setErrorMessage(result.getErrorMessage());

        return new ResponseEntity<>(classificationResultsDto, HttpStatus.OK);

        //return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    */

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public ResponseEntity<EvaluationResponse> execute(@RequestBody EvaluationRequestDto evaluationRequestDto,
                                                      HttpServletRequest request) {

        Date requestDate = new Date();
        String ipAddress = request.getRemoteAddr();

        log.info("{}", evaluationRequestDto);

        log.info("Received request for client {} at: {}", ipAddress, DATE_FORMAT.format(requestDate));

        EvaluationRequest evaluationRequest = new EvaluationRequest(ipAddress, requestDate,
                new InputData(evaluationRequestDto.getClassifier(), evaluationRequestDto.getData()),
                evaluationRequestDto.getEvaluationMethod(), evaluationRequestDto.getNumFolds(), evaluationRequestDto.getNumTests());

        return new ResponseEntity<>(ecaService.processRequest(evaluationRequest), HttpStatus.OK);
    }

}
