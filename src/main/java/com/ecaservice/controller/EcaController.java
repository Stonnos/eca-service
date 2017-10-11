package com.ecaservice.controller;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.mapping.OrikaBeanMapper;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.model.experiment.ExperimentRequestResult;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.EcaService;
import com.ecaservice.service.experiment.NotificationService;
import com.ecaservice.service.experiment.ExperimentService;
import eca.generators.SimpleDataGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    private final EcaService ecaService;
    private final OrikaBeanMapper mapper;
    private final ExperimentService experimentService;
    private final NotificationService notificationService;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with dependency spring injection.
     * @param ecaService {@link EcaService} bean
     * @param mapper     {@link OrikaBeanMapper} bean
     * @param experimentService
     * @param notificationService
     * @param experimentRepository
     */
    @Autowired
    public EcaController(EcaService ecaService, OrikaBeanMapper mapper,
                         ExperimentService experimentService,
                         NotificationService notificationService,
                         ExperimentRepository experimentRepository) {
        this.ecaService = ecaService;
        this.mapper = mapper;
        this.experimentService = experimentService;
        this.notificationService = notificationService;
        this.experimentRepository = experimentRepository;
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param evaluationRequestDto {@link EvaluationRequestDto} object
     * @param request              {@link HttpServletRequest} object
     * @return {@link ResponseEntity} object
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public ResponseEntity<EvaluationResponse> execute(@RequestBody EvaluationRequestDto evaluationRequestDto,
                                                      HttpServletRequest request) {
        log.info("Received request for client {} at: {}", request.getRemoteAddr(),
                DATE_FORMAT.format(LocalDateTime.now()));
        EvaluationRequest evaluationRequest = mapper.map(evaluationRequestDto, EvaluationRequest.class);
        evaluationRequest.setIpAddress(request.getRemoteAddr());
        EvaluationResponse evaluationResponse = ecaService.processRequest(evaluationRequest);
        log.info("Evaluation response with status [{}] was built.", evaluationResponse.getStatus());
        return new ResponseEntity<>(evaluationResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/send")
    public ResponseEntity<String> sendMessage() {
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setFirstName("Роман");
        experimentRequest.setEmail("roman.batygin@mail.ru");
        experimentRequest.setIpAddress("127.0.0.1");
        experimentRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experimentRequest.setExperimentType(ExperimentType.KNN);
        SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();
        experimentRequest.setData(simpleDataGenerator.generate());
        ExperimentRequestResult requestResult = experimentService.createExperiment(experimentRequest);

        for (Experiment experiment : experimentRepository.findAll()) {
            experimentService.processExperiment(experiment);
            notificationService.notifyByEmail(experiment);
        }

        return new ResponseEntity(requestResult, HttpStatus.OK);
    }

}
