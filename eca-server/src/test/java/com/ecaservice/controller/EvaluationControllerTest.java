package com.ecaservice.controller;

import com.ecaservice.configuation.Oauth2ResourceServerConfiguration;
import com.ecaservice.configuation.Oauth2ServerConfiguration;
import com.ecaservice.configuation.Oauth2ServerSecurityConfiguration;
import com.ecaservice.controller.web.EvaluationController;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.EvaluationLogMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.evaluation.EvaluationLogService;
import org.junit.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.inject.Inject;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Roman Batygin
 */
@WebMvcTest(controllers = EvaluationController.class)
@Import({Oauth2ResourceServerConfiguration.class, Oauth2ServerConfiguration.class,
        Oauth2ServerSecurityConfiguration.class, EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, ClassifierInfoMapperImpl.class})
public class EvaluationControllerTest extends AbstractJpaTest {

    @MockBean
    private EvaluationLogService evaluationLogService;

    @Inject
    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {
        String token = obtainAccessToken("admin", "secret");
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
