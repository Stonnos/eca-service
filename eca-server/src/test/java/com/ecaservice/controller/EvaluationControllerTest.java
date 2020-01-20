package com.ecaservice.controller;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.oauth2.MethodSecurityConfiguration;
import com.ecaservice.configuation.Oauth2ResourceServerConfiguration;
import com.ecaservice.configuation.Oauth2ServerConfiguration;
import com.ecaservice.configuation.Oauth2ServerSecurityConfiguration;
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
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

import static com.ecaservice.TestHelperUtils.TEST_UUID;
import static com.ecaservice.controller.ResponseBodyMatcher.responseBody;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EvaluationController.class)
@Oauth2TestConfiguration
@Import({EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, ClassifierInfoMapperImpl.class})
public class EvaluationControllerTest {

    @MockBean
    private EvaluationLogService evaluationLogService;
    @MockBean
    private EvaluationLogRepository evaluationLogRepository;

    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private MockMvc mockMvc;

    private String token;

    @Before
    public void init() throws Exception {
        token = obtainAccessToken("admin", "secret");
    }

    @Test
    public void testGetEvaluationLogDetailsUnauthorized() throws Exception {
        mockMvc.perform(get("/evaluation/details/{requestId}", TEST_UUID)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetEvaluationLogDetailsBadRequest() throws Exception {
        when(evaluationLogRepository.findByRequestId(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get("/evaluation/details/{requestId}", TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEvaluationLogDetailsOk() throws Exception {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog(TEST_UUID, RequestStatus.FINISHED);
        when(evaluationLogRepository.findByRequestId(TEST_UUID)).thenReturn(evaluationLog);
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        when(evaluationLogService.getEvaluationLogDetails(evaluationLog)).thenReturn(evaluationLogDetailsDto);
        mockMvc.perform(get("/evaluation/details/{requestId}", TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsBody(evaluationLogDetailsDto));
    }

    private String obtainAccessToken(String username, String password) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);

        String credentials = Base64Utils.encodeToString("client:secret".getBytes(StandardCharsets.UTF_8));

        ResultActions result = mockMvc.perform(post("/oauth/token")
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }
}
