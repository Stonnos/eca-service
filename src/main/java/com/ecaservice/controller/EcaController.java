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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;
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
    private final JavaMailSender javaMailSender;

    /**
     * Constructor with dependency spring injection.
     *  @param ecaService {@link EcaService} bean
     * @param mapper     {@link OrikaBeanMapper} bean
     * @param javaMailSender
     */
    @Autowired
    public EcaController(EcaService ecaService, OrikaBeanMapper mapper, JavaMailSender javaMailSender) {
        this.ecaService = ecaService;
        this.mapper = mapper;
        this.javaMailSender = javaMailSender;
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
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("roman.batygin@mail.ru");
            helper.setTo("r.batygin@intabia.ru");
            helper.setSubject("Hello");
            helper.setText("Hello");
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity("OK", HttpStatus.OK);
    }

}
