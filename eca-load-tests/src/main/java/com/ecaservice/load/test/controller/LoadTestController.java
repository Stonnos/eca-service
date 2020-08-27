package com.ecaservice.load.test.controller;

import com.ecaservice.load.test.dto.LoadTestRequest;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.service.LoadTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    private final LoadTestService loadTestService;

    /**
     * Creates load test.
     *
     * @param loadTestRequest - load test request
     * @return load test uuid
     */
    @ApiOperation(
            value = "Creates load test",
            notes = "Creates load test"
    )
    @PostMapping(value = "/create")
    public ResponseEntity<String> createTest(@Valid LoadTestRequest loadTestRequest) {
        log.info("Request for load test with params: {}", loadTestRequest);
        LoadTestEntity loadTestEntity = loadTestService.createTest(loadTestRequest);
        log.info("Load test has been created with uuid [{}]", loadTestEntity.getTestUuid());
        return ResponseEntity.ok(loadTestEntity.getTestUuid());
    }

}
