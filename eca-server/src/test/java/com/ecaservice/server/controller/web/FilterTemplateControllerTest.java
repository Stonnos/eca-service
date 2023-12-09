package com.ecaservice.server.controller.web;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.configuation.annotation.EnableCamundaMock;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.service.filter.dictionary.FilterDictionaries;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.TestHelperUtils.bearerHeader;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link FilterTemplateController} functionality.
 *
 * @author Roman Batygin
 */
@EnableCamundaMock
@WebMvcTest(controllers = FilterTemplateController.class)
class FilterTemplateControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/filter-templates";
    private static final String EXPERIMENT_FILTER_TEMPLATE_URL = BASE_URL + "/experiment";
    private static final String CLASSIFIERS_CONFIGURATION_HISTORY_FILTER_TEMPLATE_URL
            = BASE_URL + "/classifiers-configuration-history";
    private static final String EVALUATION_LOG_FILTER_TEMPLATE_URL = BASE_URL + "/evaluation";
    private static final String EXPERIMENT_TYPES_URL = BASE_URL + "/experiment-types";
    private static final String EVALUATION_METHODS_URL = BASE_URL + "/evaluation-methods";

    @MockBean
    private FilterTemplateService filterTemplateService;

    @Test
    void testGetExperimentFilterTemplateUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_FILTER_TEMPLATE_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetExperimentFilterTemplateNotFound() throws Exception {
        testGetFilterTemplateNotFound(EXPERIMENT_FILTER_TEMPLATE_URL, FilterTemplateType.EXPERIMENT);
    }

    @Test
    void testGetExperimentFilterTemplateOk() throws Exception {
        testGetFilterTemplateOk(EXPERIMENT_FILTER_TEMPLATE_URL, FilterTemplateType.EXPERIMENT);
    }

    @Test
    void testGetEvaluationLogFilterTemplateUnauthorized() throws Exception {
        mockMvc.perform(get(EVALUATION_LOG_FILTER_TEMPLATE_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEvaluationLogFilterTemplateNotFound() throws Exception {
        testGetFilterTemplateNotFound(EVALUATION_LOG_FILTER_TEMPLATE_URL, FilterTemplateType.EVALUATION_LOG);
    }

    @Test
    void testGetEvaluationLogFilterTemplateOk() throws Exception {
        testGetFilterTemplateOk(EVALUATION_LOG_FILTER_TEMPLATE_URL, FilterTemplateType.EVALUATION_LOG);
    }

    @Test
    void testGetExperimentTypeDictionaryUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_TYPES_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetExperimentTypeDictionaryNotFound() throws Exception {
        testGetFilterDictionaryNotFound(EXPERIMENT_TYPES_URL, FilterDictionaries.EXPERIMENT_TYPE);
    }

    @Test
    void testGetExperimentTypeDictionaryOk() throws Exception {
        testGetFilterDictionaryOk(EXPERIMENT_TYPES_URL, FilterDictionaries.EXPERIMENT_TYPE);
    }

    @Test
    void testGetEvaluationMethodsDictionaryUnauthorized() throws Exception {
        mockMvc.perform(get(EVALUATION_METHODS_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEvaluationMethodsDictionaryNotFound() throws Exception {
        testGetFilterDictionaryNotFound(EVALUATION_METHODS_URL, FilterDictionaries.EVALUATION_METHOD);
    }

    @Test
    void testGetEvaluationMethodsDictionaryOk() throws Exception {
        testGetFilterDictionaryOk(EVALUATION_METHODS_URL, FilterDictionaries.EVALUATION_METHOD);
    }

    @Test
    void testGetClassifiersConfigurationHistoryFilterTemplateUnauthorized() throws Exception {
        mockMvc.perform(get(CLASSIFIERS_CONFIGURATION_HISTORY_FILTER_TEMPLATE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetClassifiersConfigurationHistoryFilterTemplateNotFound() throws Exception {
        testGetFilterTemplateNotFound(CLASSIFIERS_CONFIGURATION_HISTORY_FILTER_TEMPLATE_URL,
                FilterTemplateType.CLASSIFIERS_CONFIGURATION_HISTORY);
    }

    @Test
    void testGetClassifiersConfigurationHistoryFilterTemplateOk() throws Exception {
        testGetFilterTemplateOk(CLASSIFIERS_CONFIGURATION_HISTORY_FILTER_TEMPLATE_URL,
                FilterTemplateType.CLASSIFIERS_CONFIGURATION_HISTORY);
    }

    private void testGetFilterTemplateNotFound(String templateUrl, String filterTemplateType)
            throws Exception {
        when(filterTemplateService.getFilterFields(filterTemplateType)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(templateUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    private void testGetFilterTemplateOk(String templateUrl, String filterTemplateType) throws Exception {
        List<FilterFieldDto> filterFieldDtoList = Collections.singletonList(TestHelperUtils.createFilterFieldDto());
        when(filterTemplateService.getFilterFields(filterTemplateType)).thenReturn(filterFieldDtoList);
        mockMvc.perform(get(templateUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(filterFieldDtoList)));
    }

    private void testGetFilterDictionaryNotFound(String templateUrl, String filterDictionaryName) throws Exception {
        when(filterTemplateService.getFilterDictionary(filterDictionaryName)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(templateUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    private void testGetFilterDictionaryOk(String templateUrl, String filterDictionaryName) throws Exception {
        FilterDictionaryDto filterDictionaryDto = TestHelperUtils.createFilterDictionaryDto();
        when(filterTemplateService.getFilterDictionary(filterDictionaryName)).thenReturn(filterDictionaryDto);
        mockMvc.perform(get(templateUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(filterDictionaryDto)));
    }
}
