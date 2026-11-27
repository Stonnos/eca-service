package com.ecaservice.ers.controller.web;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.report.EvaluationResultsHistoryReportDataFetcher;
import com.ecaservice.ers.service.EvaluationResultsHistoryService;
import com.ecaservice.ers.service.InstancesService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.EvaluationResultsHistoryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ecaservice.ers.TestHelperUtils.createHistoryPageDto;
import static com.ecaservice.ers.TestHelperUtils.createInstancesInfoPageDto;
import static com.ecaservice.ers.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.ers.TestHelperUtils.loadEvaluationResultsHistoryFilterFields;
import static com.ecaservice.ers.dictionary.FilterDictionaries.EVALUATION_RESULTS_HISTORY_TEMPLATE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link EvaluationResultsHistoryController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = EvaluationResultsHistoryController.class)
class EvaluationResultsHistoryControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/evaluation-results";
    private static final String HISTORY_PAGE_URL = BASE_URL + "/history";
    private static final String FILTER_FIELDS_URL = BASE_URL + "/filter-templates/fields";
    private static final String INSTANCES_LIST_URL = BASE_URL + "/instances/list";
    private static final String DOWNLOAD_REPORT_URL = BASE_URL + "/report/download";

    @MockBean
    private FilterTemplateService filterTemplateService;

    @MockBean
    private EvaluationResultsHistoryService evaluationResultsHistoryService;

    @MockBean
    private InstancesService instancesService;

    @MockBean
    private EvaluationResultsHistoryReportDataFetcher evaluationResultsHistoryReportDataFetcher;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetEvaluationResultsHistoryPageUnauthorized() throws Exception {
        PageRequestDto request = createPageRequestDto();
        mockMvc.perform(post(HISTORY_PAGE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEvaluationResultsHistoryPage() throws Exception {
        PageRequestDto request = createPageRequestDto();
        PageDto<EvaluationResultsHistoryDto> response = createHistoryPageDto();
        when(evaluationResultsHistoryService.getNextPage(request)).thenReturn(response);

        mockMvc.perform(post(HISTORY_PAGE_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testGetEvaluationResultsHistoryFilterUnauthorized() throws Exception {
        mockMvc.perform(get(FILTER_FIELDS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEvaluationResultsHistoryFilter() throws Exception {
        List<FilterFieldDto> response = loadEvaluationResultsHistoryFilterFields();
        when(filterTemplateService.getFilterFields(EVALUATION_RESULTS_HISTORY_TEMPLATE)).thenReturn(response);
        mockMvc.perform(get(FILTER_FIELDS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testGetInstancesInfoPageUnauthorized() throws Exception {
        PageRequestDto request = createPageRequestDto();
        mockMvc.perform(post(INSTANCES_LIST_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetInstancesInfoPage() throws Exception {
        PageRequestDto request = createPageRequestDto();
        PageDto<InstancesInfoDto> response = createInstancesInfoPageDto();
        when(instancesService.getNextPage(request)).thenReturn(response);

        mockMvc.perform(post(INSTANCES_LIST_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testDownloadEvaluationResultsHistoryReportUnauthorized() throws Exception {
        PageRequestDto request = createPageRequestDto();
        mockMvc.perform(post(DOWNLOAD_REPORT_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDownloadEvaluationResultsHistoryReport() throws Exception {
        mockMvc.perform(post(DOWNLOAD_REPORT_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(createPageRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }
}

