package com.ecaservice.controller;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.mapping.OrikaBeanMapper;
import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.service.EcaService;
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
import java.time.LocalDateTime;

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
    private final OrikaBeanMapper mapper;

    /**
     * Constructor with dependency spring injection.
     *
     * @param ecaService {@link EcaService} bean
     * @param mapper     {@link OrikaBeanMapper} bean
     */
    @Autowired
    public EcaController(EcaService ecaService, OrikaBeanMapper mapper) {
        this.ecaService = ecaService;
        this.mapper = mapper;
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

        LocalDateTime requestDate = LocalDateTime.now();
        String ipAddress = request.getRemoteAddr();

        //log.info("Received request for client {} at: {}", ipAddress, DATE_FORMAT.format(requestDate));

        EvaluationRequest evaluationRequest = mapper.map(evaluationRequestDto, EvaluationRequest.class);
        evaluationRequest.setRequestDate(requestDate);
        evaluationRequest.setIpAddress(ipAddress);

        EvaluationResponse evaluationResponse = ecaService.processRequest(evaluationRequest);

        log.info("Evaluation response with status [{}] was built.", evaluationResponse.getStatus());

        return new ResponseEntity<>(evaluationResponse, HttpStatus.OK);
    }

}
