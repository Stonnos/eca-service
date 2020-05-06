package com.ecaservice.controller.web;

import com.ecaservice.report.EvaluationLogsBaseReportDataFetcher;
import com.ecaservice.report.ExperimentsBaseReportDataFetcher;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

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
@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest extends PageRequestControllerTest {

    private static final String BASE_URL = "/reports";
    private static final String EXPERIMENTS_REPORT_URL = BASE_URL + "/experiments";
    private static final String EVALUATIONS_REPORT_URL = BASE_URL + "/evaluations";

    @MockBean
    private ExperimentsBaseReportDataFetcher experimentsBaseReportDataFetcher;
    @MockBean
    private EvaluationLogsBaseReportDataFetcher evaluationLogsBaseReportDataFetcher;

    @Test
    public void testDownloadExperimentsReportUnauthorized() throws Exception {
        testGetPageUnauthorized(EXPERIMENTS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadExperimentsReportWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(EXPERIMENTS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadExperimentsReportWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(EXPERIMENTS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadExperimentsReportWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(EXPERIMENTS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadExperimentsReportWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(EXPERIMENTS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadExperimentsReportWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(EXPERIMENTS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadExperimentsReportWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(EXPERIMENTS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadExperimentsReportOk() throws Exception {
        when(experimentsBaseReportDataFetcher.fetchReportData(any(PageRequestDto.class))).thenReturn(
                new BaseReportBean<>());
        mockMvc.perform(get(EXPERIMENTS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    public void testDownloadEvaluationsReportUnauthorized() throws Exception {
        testGetPageUnauthorized(EVALUATIONS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadEvaluationsReportWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(EVALUATIONS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadEvaluationsReportWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(EVALUATIONS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadEvaluationsReportWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(EVALUATIONS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadEvaluationsReportWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(EVALUATIONS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadEvaluationsReportWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(EVALUATIONS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadEvaluationsReportWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(EVALUATIONS_REPORT_URL, Collections.emptyMap());
    }

    @Test
    public void testDownloadEvaluationsReportOk() throws Exception {
        when(evaluationLogsBaseReportDataFetcher.fetchReportData(any(PageRequestDto.class))).thenReturn(
                new BaseReportBean<>());
        mockMvc.perform(get(EVALUATIONS_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }
}
