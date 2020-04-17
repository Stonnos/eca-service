package com.ecaservice.controller.web;

import com.ecaservice.model.entity.EvaluationLog_;
import com.ecaservice.report.BaseReportGenerator;
import com.ecaservice.report.EvaluationLogsBaseReportDataFetcher;
import com.ecaservice.report.ExperimentsBaseReportDataFetcher;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.OutputStream;

import static com.ecaservice.PageRequestUtils.FILTER_MATCH_MODE_PARAM;
import static com.ecaservice.PageRequestUtils.FILTER_NAME_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE_PARAM;
import static com.ecaservice.TestHelperUtils.bearerHeader;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ReportController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({BaseReportGenerator.class})
@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/reports";
    private static final String EXPERIMENTS_REPORT_URL = BASE_URL + "/experiments";
    private static final String EVALUATIONS_REPORT_URL = BASE_URL + "/evaluations";

    @MockBean
    private ExperimentsBaseReportDataFetcher experimentsBaseReportDataFetcher;
    @MockBean
    private EvaluationLogsBaseReportDataFetcher evaluationLogsBaseReportDataFetcher;
    
    @Test
    public void testDownloadExperimentsReportUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDownloadExperimentsReportWithNullPageNumber() throws Exception {
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadExperimentsReportWithNullPageSize() throws Exception {
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadExperimentsReportWithZeroPageSize() throws Exception {
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadExperimentsReportWithNegativePageNumber() throws Exception {
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(-1))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadExperimentsReportWithEmptyFilterRequestName() throws Exception {
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, StringUtils.EMPTY)
                .param(FILTER_MATCH_MODE_PARAM, MatchMode.RANGE.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadExperimentsReportWithNullMatchMode() throws Exception {
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, EvaluationLog_.CREATION_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadExperimentsReportOk() throws Exception {
        when(experimentsBaseReportDataFetcher.fetchReportData(any(PageRequestDto.class))).thenReturn(
                new BaseReportBean<>());
        PowerMockito.mockStatic(BaseReportGenerator.class);
        PowerMockito.doNothing().when(BaseReportGenerator.class, "generateExperimentsReport", any(BaseReportBean
                .class), any(OutputStream.class));
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    public void testDownloadEvaluationsReportUnauthorized() throws Exception {
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDownloadEvaluationsReportWithNullPageNumber() throws Exception {
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadEvaluationsReportWithNullPageSize() throws Exception {
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadEvaluationsReportWithZeroPageSize() throws Exception {
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadEvaluationsReportWithNegativePageNumber() throws Exception {
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(-1))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadEvaluationsReportWithEmptyFilterRequestName() throws Exception {
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, StringUtils.EMPTY)
                .param(FILTER_MATCH_MODE_PARAM, MatchMode.RANGE.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadEvaluationsReportWithNullMatchMode() throws Exception {
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, EvaluationLog_.CREATION_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDownloadEvaluationsReportOk() throws Exception {
        when(evaluationLogsBaseReportDataFetcher.fetchReportData(any(PageRequestDto.class))).thenReturn(
                new BaseReportBean<>());
        PowerMockito.mockStatic(BaseReportGenerator.class);
        PowerMockito.doNothing().when(BaseReportGenerator.class, "generateEvaluationLogsReport", any(BaseReportBean
                .class), any(OutputStream.class));
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }
}
