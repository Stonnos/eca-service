package com.ecaservice.controller.web;

import com.ecaservice.report.ClassifierOptionsRequestsBaseReportDataFetcher;
import com.ecaservice.report.EvaluationLogsBaseReportDataFetcher;
import com.ecaservice.report.ExperimentsBaseReportDataFetcher;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

import static com.ecaservice.TestHelperUtils.bearerHeader;
import static com.ecaservice.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.TestHelperUtils.createReportBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ReportController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = ReportController.class)
class ReportControllerTest extends PageRequestControllerTest {

    private static final String DOWNLOAD_REPORT_URL = "/reports/download";
    private static final String REPORT_TYPE_PARAM = "reportType";

    @MockBean
    private ExperimentsBaseReportDataFetcher experimentsBaseReportDataFetcher;
    @MockBean
    private EvaluationLogsBaseReportDataFetcher evaluationLogsBaseReportDataFetcher;
    @MockBean
    private ClassifierOptionsRequestsBaseReportDataFetcher classifierOptionsRequestsBaseReportDataFetcher;

    @Override
    public void before() {
        when(experimentsBaseReportDataFetcher.getReportType()).thenReturn(ReportType.EXPERIMENTS);
        when(evaluationLogsBaseReportDataFetcher.getReportType()).thenReturn(ReportType.EVALUATION_LOGS);
        when(classifierOptionsRequestsBaseReportDataFetcher.getReportType()).thenReturn(ReportType.CLASSIFIERS_OPTIONS_REQUESTS);
    }

    @Test
    void testDownloadReportUnauthorized() throws Exception {
        testGetPageUnauthorized(DOWNLOAD_REPORT_URL,
                Collections.singletonMap(REPORT_TYPE_PARAM,
                        Collections.singletonList(ReportType.EVALUATION_LOGS.name())));
    }

    @Test
    void testDownloadReportWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(DOWNLOAD_REPORT_URL, Collections.singletonMap(REPORT_TYPE_PARAM,
                Collections.singletonList(ReportType.EVALUATION_LOGS.name())));
    }

    @Test
    void testDownloadReportWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(DOWNLOAD_REPORT_URL, Collections.singletonMap(REPORT_TYPE_PARAM,
                Collections.singletonList(ReportType.EVALUATION_LOGS.name())));
    }

    @Test
    void testDownloadReportWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(DOWNLOAD_REPORT_URL, Collections.singletonMap(REPORT_TYPE_PARAM,
                Collections.singletonList(ReportType.EVALUATION_LOGS.name())));
    }

    @Test
    void testDownloadReportWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(DOWNLOAD_REPORT_URL, Collections.singletonMap(REPORT_TYPE_PARAM,
                Collections.singletonList(ReportType.EVALUATION_LOGS.name())));
    }

    @Test
    void testDownloadReportWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(DOWNLOAD_REPORT_URL, Collections.singletonMap(REPORT_TYPE_PARAM,
                Collections.singletonList(ReportType.EVALUATION_LOGS.name())));
    }

    @Test
    void testDownloadReportWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(DOWNLOAD_REPORT_URL, Collections.singletonMap(REPORT_TYPE_PARAM,
                Collections.singletonList(ReportType.EVALUATION_LOGS.name())));
    }

    @Test
    void testDownloadReportWithNullReportType() throws Exception {
        mockMvc.perform(post(DOWNLOAD_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadExperimentsReportOk() throws Exception {
        when(experimentsBaseReportDataFetcher.fetchReportData(any(PageRequestDto.class))).thenReturn(
                createReportBean());
        testDownloadReportOk(ReportType.EXPERIMENTS);
    }

    @Test
    void testDownloadEvaluationsReportOk() throws Exception {
        when(evaluationLogsBaseReportDataFetcher.fetchReportData(any(PageRequestDto.class))).thenReturn(
                createReportBean());
        testDownloadReportOk(ReportType.EVALUATION_LOGS);
    }

    @Test
    void testDownloadClassifierOptionsRequestsReportOk() throws Exception {
        when(classifierOptionsRequestsBaseReportDataFetcher.fetchReportData(any(PageRequestDto.class))).thenReturn(
                createReportBean());
        testDownloadReportOk(ReportType.CLASSIFIERS_OPTIONS_REQUESTS);
    }

    private void testDownloadReportOk(ReportType reportType) throws Exception {
        mockMvc.perform(post(DOWNLOAD_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(REPORT_TYPE_PARAM, reportType.name())
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }
}
