package com.ecaservice.server.controller.web;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.ExperimentProgressMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.experiment.ClassifyExperimentResultsInstanceService;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentRequestWebApiService;
import com.ecaservice.server.service.experiment.ExperimentResultsRocCurveDataProvider;
import com.ecaservice.server.service.experiment.ExperimentResultsService;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentProgressDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.S3ContentResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.server.TestHelperUtils.buildExperimentRequestDto;
import static com.ecaservice.server.TestHelperUtils.createPageRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ExperimentController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = ExperimentController.class)
@Import({ExperimentMapperImpl.class, ExperimentProgressMapperImpl.class, DateTimeConverter.class,
        InstancesInfoMapperImpl.class, AppProperties.class})
class ExperimentControllerTest extends PageRequestControllerTest {

    private static final String BASE_URL = "/experiment";
    private static final String DETAILS_URL = BASE_URL + "/details/{id}";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String CREATE_EXPERIMENT_URL = BASE_URL + "/create";
    private static final String EXPERIMENT_RESULTS_DETAILS_URL = BASE_URL + "/results/details/{id}";
    private static final String ERS_REPORT_URL = BASE_URL + "/ers-report/{id}";
    private static final String REQUEST_STATUS_STATISTICS_URL = BASE_URL + "/request-statuses-statistics";
    private static final String EXPERIMENT_TYPES_STATISTICS_URL = BASE_URL + "/statistics";
    private static final String EXPERIMENT_PROGRESS_URL = BASE_URL + "/progress/{id}";
    private static final String EXPERIMENT_RESULTS_CONTENT_URL = BASE_URL + "/results-content/{id}";

    private static final long EXPERIMENT_RESULTS_ID = 1L;
    private static final int PROGRESS_VALUE = 100;
    private static final long ID = 1L;
    private static final String CONTENT_URL = "http://localhost:9000/content";

    @MockBean
    private ExperimentDataService experimentDataService;
    @MockBean
    private ExperimentResultsRocCurveDataProvider experimentResultsRocCurveDataProvider;
    @MockBean
    private ClassifyExperimentResultsInstanceService classifyExperimentResultsInstanceService;
    @MockBean
    private ExperimentResultsService experimentResultsService;
    @MockBean
    private ExperimentProgressService experimentProgressService;
    @MockBean
    private ExperimentRequestWebApiService experimentRequestWebApiService;
    @MockBean
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @Autowired
    private ExperimentMapper experimentMapper;

    @Test
    void testGetExperimentDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, ID)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetExperimentDetailsNotFound() throws Exception {
        when(experimentDataService.getById(ID)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(DETAILS_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetExperimentDetailsOk() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(experimentDataService.getById(ID)).thenReturn(experiment);
        ExperimentDto experimentDto = experimentMapper.map(experiment);
        mockMvc.perform(get(DETAILS_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(experimentDto)));
    }

    @Test
    void testCreateExperimentUnauthorized() throws Exception {
        var experimentRequestDto = buildExperimentRequestDto();
        mockMvc.perform(post(CREATE_EXPERIMENT_URL)
                .content(objectMapper.writeValueAsString(experimentRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateExperimentSuccess() throws Exception {
        var experimentRequestDto = buildExperimentRequestDto();
        CreateExperimentResultDto expected = CreateExperimentResultDto.builder()
                .id(ID)
                .requestId(UUID.randomUUID().toString())
                .build();
        when(experimentRequestWebApiService.createExperiment(experimentRequestDto)).thenReturn(expected);
        mockMvc.perform(post(CREATE_EXPERIMENT_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(experimentRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testCreateExperimentBadRequestWithNullExperimentType() throws Exception {
        var experimentRequestDto = buildExperimentRequestDto();
        experimentRequestDto.setExperimentType(null);
        internalTestCreateExperimentBadRequest(experimentRequestDto);
    }

    @Test
    void testCreateExperimentBadRequestWithNullEvaluationMethod() throws Exception {
        var experimentRequestDto = buildExperimentRequestDto();
        experimentRequestDto.setEvaluationMethod(null);
        internalTestCreateExperimentBadRequest(experimentRequestDto);
    }

    @Test
    void testCreateExperimentBadRequestWithEmptyInstancesUuid() throws Exception {
        var experimentRequestDto = buildExperimentRequestDto();
        experimentRequestDto.setInstancesUuid(StringUtils.EMPTY);
        internalTestCreateExperimentBadRequest(experimentRequestDto);
    }

    @Test
    void testCreateExperimentBadRequestWithInvalidInstancesUuidPattern() throws Exception {
        var experimentRequestDto = buildExperimentRequestDto();
        experimentRequestDto.setInstancesUuid("abc");
        internalTestCreateExperimentBadRequest(experimentRequestDto);
    }

    @Test
    void testGetExperimentsUnauthorized() throws Exception {
        testGetPageUnauthorized(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetExperimentsWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetExperimentsWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetExperimentsWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetExperimentsWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetExperimentsWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetExperimentsWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetExperimentsLogsOk() throws Exception {
        Page<Experiment> experimentPage = Mockito.mock(Page.class);
        when(experimentPage.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<Experiment> experiments =
                Collections.singletonList(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        PageDto<ExperimentDto> expected = PageDto.of(experimentMapper.map(experiments), PAGE_NUMBER, TOTAL_ELEMENTS);
        when(experimentPage.getContent()).thenReturn(experiments);
        when(experimentDataService.getExperimentsPage(any(PageRequestDto.class))).thenReturn(expected);
        mockMvc.perform(post(LIST_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(createPageRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testExperimentsRequestStatusesStatisticsUnauthorized() throws Exception {
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testExperimentsRequestStatusesStatisticsOk() throws Exception {
        RequestStatusStatisticsDto requestStatusStatisticsDto = new RequestStatusStatisticsDto();
        when(experimentDataService.getRequestStatusesStatistics()).thenReturn(requestStatusStatisticsDto);
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requestStatusStatisticsDto)));
    }

    @Test
    void testGetExperimentResultsDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_RESULTS_DETAILS_URL, EXPERIMENT_RESULTS_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetExperimentResultsDetailsNotFound() throws Exception {
        when(experimentResultsEntityRepository.findById(EXPERIMENT_RESULTS_ID)).thenReturn(Optional.empty());
        mockMvc.perform(get(EXPERIMENT_RESULTS_DETAILS_URL, EXPERIMENT_RESULTS_ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetExperimentResultsDetailsOk() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        ExperimentResultsDetailsDto experimentResultsDetailsDto = new ExperimentResultsDetailsDto();
        experimentResultsDetailsDto.setExperimentDto(experimentMapper.map(experiment));
        experimentResultsDetailsDto.setEvaluationResultsDto(
                TestHelperUtils.createEvaluationResultsDto(EvaluationResultsStatus.RESULTS_RECEIVED));
        when(experimentResultsEntityRepository.findById(EXPERIMENT_RESULTS_ID)).thenReturn(
                Optional.of(experimentResultsEntity));
        when(experimentResultsService.getExperimentResultsDetails(experimentResultsEntity)).thenReturn(
                experimentResultsDetailsDto);
        mockMvc.perform(get(EXPERIMENT_RESULTS_DETAILS_URL, EXPERIMENT_RESULTS_ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(experimentResultsDetailsDto)));
    }

    @Test
    void testGetErsReportUnauthorized() throws Exception {
        mockMvc.perform(get(ERS_REPORT_URL, ID)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetErsReportForNotExistingExperiment() throws Exception {
        when(experimentDataService.getById(ID)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(ERS_REPORT_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetErsReportOk() throws Exception {
        ExperimentErsReportDto expected = new ExperimentErsReportDto();
        when(experimentDataService.getById(ID)).thenReturn(new Experiment());
        when(experimentResultsService.getErsReport(any(Experiment.class))).thenReturn(expected);
        mockMvc.perform(get(ERS_REPORT_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetExperimentTypesStatisticsUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_TYPES_STATISTICS_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetExperimentTypesStatisticsOk() throws Exception {
        ChartDto chartDto = ChartDto.builder()
                .total(1L)
                .dataItems(Collections.singletonList(new ChartDataDto("Item", "Item", 1L)))
                .build();
        when(experimentDataService.getExperimentsStatistics(null, null))
                .thenReturn(chartDto);
        mockMvc.perform(get(EXPERIMENT_TYPES_STATISTICS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(chartDto)));

    }

    @Test
    void testGetExperimentProgressUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_PROGRESS_URL, ID)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetExperimentProgressForNotExistingExperiment() throws Exception {
        when(experimentDataService.getById(ID)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(EXPERIMENT_PROGRESS_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetExperimentProgressForNotExistingExperimentProgress() throws Exception {
        when(experimentDataService.getById(ID)).thenReturn(new Experiment());
        when(experimentProgressService.getExperimentProgress(any(Experiment.class)))
                .thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(EXPERIMENT_PROGRESS_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetExperimentProgressOk() throws Exception {
        ExperimentProgressDto expected = new ExperimentProgressDto();
        expected.setFinished(true);
        expected.setProgress(PROGRESS_VALUE);
        when(experimentDataService.getById(ID)).thenReturn(new Experiment());
        ExperimentProgressEntity experimentProgressEntity = new ExperimentProgressEntity();
        experimentProgressEntity.setFinished(true);
        experimentProgressEntity.setProgress(PROGRESS_VALUE);
        when(experimentProgressService.getExperimentProgress(any(Experiment.class))).thenReturn(
                experimentProgressEntity);
        mockMvc.perform(get(EXPERIMENT_PROGRESS_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetExperimentResultsContentUrlUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_RESULTS_CONTENT_URL, ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetExperimentResultsContentUrlForNotExistingExperiment() throws Exception {
        when(experimentDataService.getExperimentResultsContentUrl(ID)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(EXPERIMENT_RESULTS_CONTENT_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetExperimentResultsContentUrlOk() throws Exception {
        var s3ContentResponseDto = S3ContentResponseDto.builder()
                .contentUrl(CONTENT_URL)
                .build();
        when(experimentDataService.getExperimentResultsContentUrl(ID)).thenReturn(s3ContentResponseDto);
        mockMvc.perform(get(EXPERIMENT_RESULTS_CONTENT_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(s3ContentResponseDto)));
    }

    private void internalTestCreateExperimentBadRequest(CreateExperimentRequestDto experimentRequestDto)
            throws Exception {
        mockMvc.perform(post(CREATE_EXPERIMENT_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(experimentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
