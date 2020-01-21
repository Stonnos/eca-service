package com.ecaservice.controller.web;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.configuation.annotation.Oauth2TestConfiguration;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.service.filter.dictionary.FilterDictionaries;
import com.ecaservice.token.TokenService;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.TestHelperUtils.bearerHeader;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link FilterTemplateController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = FilterTemplateController.class)
@Oauth2TestConfiguration
public class FilterTemplateControllerTest {

    private static final String BASE_URL = "/filter-templates";
    private static final String EXPERIMENT_FILTER_TEMPLATE_URL = BASE_URL + "/experiment";
    private static final String EVALUATION_LOG_FILTER_TEMPLATE_URL = BASE_URL + "/evaluation";
    private static final String CLASSIFIER_OPTIONS_REQUEST_FILTER_TEMPLATE_URL =
            BASE_URL + "/classifier-options-request";
    private static final String EXPERIMENT_TYPES_URL = BASE_URL + "/experiment-types";
    private static final String EVALUATION_METHODS_URL = BASE_URL + "/evaluation-methods";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private FilterService filterService;

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
    public void testGetExperimentFilterTemplateUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_FILTER_TEMPLATE_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperimentFilterTemplateBadRequest() throws Exception {
        testGetFilterTemplateBadRequest(EXPERIMENT_FILTER_TEMPLATE_URL, FilterTemplateType.EXPERIMENT);
    }

    @Test
    public void testGetExperimentFilterTemplateOk() throws Exception {
        testGetFilterTemplateOk(EXPERIMENT_FILTER_TEMPLATE_URL, FilterTemplateType.EXPERIMENT);
    }

    @Test
    public void testGetEvaluationLogFilterTemplateUnauthorized() throws Exception {
        mockMvc.perform(get(EVALUATION_LOG_FILTER_TEMPLATE_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetEvaluationLogFilterTemplateBadRequest() throws Exception {
        testGetFilterTemplateBadRequest(EVALUATION_LOG_FILTER_TEMPLATE_URL, FilterTemplateType.EVALUATION_LOG);
    }

    @Test
    public void testGetEvaluationLogFilterTemplateOk() throws Exception {
        testGetFilterTemplateOk(EVALUATION_LOG_FILTER_TEMPLATE_URL, FilterTemplateType.EVALUATION_LOG);
    }

    @Test
    public void testGetClassifierOptionsRequestFilterTemplateUnauthorized() throws Exception {
        mockMvc.perform(get(CLASSIFIER_OPTIONS_REQUEST_FILTER_TEMPLATE_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetClassifierOptionsRequestFilterTemplateBadRequest() throws Exception {
        testGetFilterTemplateBadRequest(CLASSIFIER_OPTIONS_REQUEST_FILTER_TEMPLATE_URL,
                FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST);
    }

    @Test
    public void testGetClassifierOptionsRequestFilterTemplateOk() throws Exception {
        testGetFilterTemplateOk(CLASSIFIER_OPTIONS_REQUEST_FILTER_TEMPLATE_URL,
                FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST);
    }

    @Test
    public void testGetExperimentTypeDictionaryUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_TYPES_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperimentTypeDictionaryBadRequest() throws Exception {
        testGetFilterDictionaryBadRequest(EXPERIMENT_TYPES_URL, FilterDictionaries.EXPERIMENT_TYPE);
    }

    @Test
    public void testGetExperimentTypeDictionaryOk() throws Exception {
        testGetFilterDictionaryOk(EXPERIMENT_TYPES_URL, FilterDictionaries.EXPERIMENT_TYPE);
    }

    @Test
    public void testGetEvaluationMethodsDictionaryUnauthorized() throws Exception {
        mockMvc.perform(get(EVALUATION_METHODS_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetEvaluationMethodsDictionaryBadRequest() throws Exception {
        testGetFilterDictionaryBadRequest(EVALUATION_METHODS_URL, FilterDictionaries.EVALUATION_METHOD);
    }

    @Test
    public void testGetEvaluationMethodsDictionaryOk() throws Exception {
        testGetFilterDictionaryOk(EVALUATION_METHODS_URL, FilterDictionaries.EVALUATION_METHOD);
    }

    private void testGetFilterTemplateBadRequest(String templateUrl, FilterTemplateType filterTemplateType)
            throws Exception {
        when(filterService.getFilterFields(filterTemplateType)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(get(templateUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    private void testGetFilterTemplateOk(String templateUrl, FilterTemplateType filterTemplateType) throws Exception {
        List<FilterFieldDto> filterFieldDtoList = Collections.singletonList(TestHelperUtils.createFilterFieldDto());
        when(filterService.getFilterFields(filterTemplateType)).thenReturn(filterFieldDtoList);
        mockMvc.perform(get(templateUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(filterFieldDtoList)));
    }

    private void testGetFilterDictionaryBadRequest(String templateUrl, String filterDictionaryName) throws Exception {
        when(filterService.getFilterDictionary(filterDictionaryName)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(get(templateUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    private void testGetFilterDictionaryOk(String templateUrl, String filterDictionaryName) throws Exception {
        FilterDictionaryDto filterDictionaryDto = TestHelperUtils.createFilterDictionaryDto();
        when(filterService.getFilterDictionary(filterDictionaryName)).thenReturn(filterDictionaryDto);
        mockMvc.perform(get(templateUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(filterDictionaryDto)));
    }
}
