package com.ecaservice.load.test.controller;

import com.ecaservice.load.test.dto.LoadTestRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Load tests controller.
 *
 * @author Roman Batygin
 */
@Api(tags = "API for load tests execution")
@Slf4j
@RestController
@RequestMapping("/load-tests")
@RequiredArgsConstructor
public class LoadTestController {

    /**
     * Creates load test request.
     *
     * @param loadTestRequest - load test request
     */
    @ApiOperation(
            value = "Creates load test request",
            notes = "Creates load test request"
    )
    @GetMapping(value = "/create")
    public ResponseEntity<String> createTest(@Valid LoadTestRequest loadTestRequest) {
        log.info("Request for load test with params: {}", loadTestRequest);
        return ResponseEntity.ok("");
    }

}
