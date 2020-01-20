package com.ecaservice.controller;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.configuation.annotation.Oauth2TestConfiguration;
import com.ecaservice.controller.web.EvaluationController;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.EvaluationLogMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.token.TokenService;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static com.ecaservice.TestHelperUtils.TEST_UUID;
import static com.ecaservice.TestHelperUtils.bearerHeader;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for cheking {@link EvaluationController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EvaluationController.class)
@Oauth2TestConfiguration
@Import({TokenService.class, EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, ClassifierInfoMapperImpl.class})
public class EvaluationControllerTest {

    private static final String DETAILS_URL = "/evaluation/details/{requestId}";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private EvaluationLogService evaluationLogService;
    @MockBean
    private EvaluationLogRepository evaluationLogRepository;

    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private TokenService tokenService;
    @Inject
    private MockMvc mockMvc;

    private String accessToken;

    @Before
    public void init() throws Exception {
        accessToken = tokenService.obtainAccessToken();
    }

    @Test
    public void testGetEvaluationLogDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, TEST_UUID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetEvaluationLogDetailsBadRequest() throws Exception {
        when(evaluationLogRepository.findByRequestId(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEvaluationLogDetailsOk() throws Exception {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog(TEST_UUID, RequestStatus.FINISHED);
        when(evaluationLogRepository.findByRequestId(TEST_UUID)).thenReturn(evaluationLog);
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        when(evaluationLogService.getEvaluationLogDetails(evaluationLog)).thenReturn(evaluationLogDetailsDto);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(evaluationLogDetailsDto)));
    }
}
