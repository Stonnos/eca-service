package com.ecaservice.server.controller.web;

import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.server.configuation.annotation.EnableCamundaMock;
import com.ecaservice.server.service.ers.EvaluationResultsRequestPathService;
import com.ecaservice.web.dto.model.RoutePathDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createPageRequestDto;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link InstancesInfoController} functionality.
 *
 * @author Roman Batygin
 */
@EnableCamundaMock
@WebMvcTest(controllers = EvaluationResultsRequestPathController.class)
class EvaluationResultsRequestPathControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/evaluation-results";
    private static final String EVALUATION_RESULTS_REQUEST_PATH_URL = BASE_URL + "/request-path/{resultId}";

    @MockBean
    private EvaluationResultsRequestPathService evaluationResultsRequestPathService;

    @Test
    void testGetEvaluationResultsRequestPathUnauthorized() throws Exception {
        mockMvc.perform(get(EVALUATION_RESULTS_REQUEST_PATH_URL, UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEvaluationResultsRequestPathOk() throws Exception {
        RoutePathDto routePathDto = new RoutePathDto("/path");
        when(evaluationResultsRequestPathService.getEvaluationResultsRequestPath(anyString())).thenReturn(routePathDto);
        mockMvc.perform(get(EVALUATION_RESULTS_REQUEST_PATH_URL, UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(routePathDto)));
    }
}
