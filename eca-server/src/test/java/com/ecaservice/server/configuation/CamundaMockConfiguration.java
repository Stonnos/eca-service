package com.ecaservice.server.configuation;

import org.camunda.bpm.spring.boot.starter.SpringBootProcessApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class CamundaMockConfiguration {

    @MockBean
    private SpringBootProcessApplication springBootProcessApplication;
}
