package com.ecaservice.ers.controller;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.dto.ResponseStatus;
import com.ecaservice.ers.service.ClassifierOptionsRequestService;
import com.ecaservice.ers.service.EvaluationResultsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.ers.TestHelperUtils.buildEvaluationResultsReport;
import static com.ecaservice.ers.TestHelperUtils.buildEvaluationResultsResponse;
import static com.ecaservice.ers.TestHelperUtils.buildGetEvaluationResultsRequest;
import static com.ecaservice.ers.TestHelperUtils.buildGetEvaluationResultsResponse;
import static com.ecaservice.ers.TestHelperUtils.createClassifierOptionsRequest;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link EvaluationResultsController} functionality.
 *
 * @author Roman Batygin
 */
@EnableGlobalExceptionHandler
@WebMvcTest(controllers = EvaluationResultsController.class)
class EvaluationResultsControllerTest {

    private static final String BASE_URL = "/api";
    private static final String SAVE_EVALUATION_RESULTS_REQUEST_URL = BASE_URL + "/save";
    private static final String GET_EVALUATION_RESULTS_REQUEST_URL = BASE_URL + "/results";
    private static final String OPTIMAL_CLASSIFIER_OPTIONS_REQUEST_URL = BASE_URL + "/optimal-classifier-options";

    @MockBean
    private EvaluationResultsService evaluationResultsService;
    @MockBean
    private ClassifierOptionsRequestService classifierOptionsRequestService;

    @Inject
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSaveEvaluationResultsReport() throws Exception {
        EvaluationResultsRequest request = buildEvaluationResultsReport(UUID.randomUUID().toString());
        EvaluationResultsResponse response =
                buildEvaluationResultsResponse(request.getRequestId(), ResponseStatus.SUCCESS);
        when(evaluationResultsService.saveEvaluationResults(request)).thenReturn(response);
        mockMvc.perform(post(SAVE_EVALUATION_RESULTS_REQUEST_URL)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testGetEvaluationResultsReport() throws Exception {
        GetEvaluationResultsRequest request = buildGetEvaluationResultsRequest(UUID.randomUUID().toString());
        GetEvaluationResultsResponse response = buildGetEvaluationResultsResponse(request.getRequestId());
        when(evaluationResultsService.getEvaluationResultsResponse(request)).thenReturn(response);
        mockMvc.perform(post(GET_EVALUATION_RESULTS_REQUEST_URL)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testFindOptimalClassifierOptions() throws Exception {
        ClassifierOptionsRequest request = createClassifierOptionsRequest(EvaluationMethod.CROSS_VALIDATION);
        ClassifierOptionsResponse response = new ClassifierOptionsResponse();
        response.setRequestId(UUID.randomUUID().toString());
        response.setStatus(ResponseStatus.SUCCESS);
        when(classifierOptionsRequestService.findClassifierOptions(request)).thenReturn(response);
        mockMvc.perform(post(OPTIMAL_CLASSIFIER_OPTIONS_REQUEST_URL)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
