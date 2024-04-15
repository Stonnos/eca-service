package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.bpm.model.ExperimentModel;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.filter.ExperimentFilter;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.filter.dictionary.FilterDictionaries;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.S3ContentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.AbstractEvaluationEntity_.CREATION_DATE;
import static com.ecaservice.server.model.entity.Experiment_.EXPERIMENT_TYPE;
import static com.ecaservice.server.model.entity.FilterTemplateType.EXPERIMENT;
import static com.ecaservice.server.util.QueryHelper.buildGroupByStatisticsQuery;
import static com.ecaservice.server.util.StatisticsHelper.calculateChartData;
import static com.ecaservice.server.util.StatisticsHelper.calculateRequestStatusesStatistics;

/**
 * Experiment data service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ExperimentDataService {

    private final ExperimentRepository experimentRepository;
    private final ObjectStorageService objectStorageService;
    private final EntityManager entityManager;
    private final AppProperties appProperties;
    private final FilterTemplateService filterTemplateService;
    private final ExperimentMapper experimentMapper;

    /**
     * Removes experiment model file from object storage.
     *
     * @param experiment - experiment entity
     */
    public void removeExperimentModel(Experiment experiment) {
        try {
            log.info("Starting to remove experiment [{}] model file", experiment.getRequestId());
            String experimentPath = experiment.getModelPath();
            objectStorageService.removeObject(experimentPath);
            experiment.setModelPath(null);
            experiment.setExperimentDownloadUrl(null);
            experiment.setDeletedDate(LocalDateTime.now());
            experimentRepository.save(experiment);
            log.info("Experiment [{}] model file has been deleted", experiment.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while remove experiment [{}] model file: {}", experiment.getRequestId(),
                    ex.getMessage());
        }
    }

    /**
     * Gets experiments page.
     *
     * @param pageRequestDto - page request dto
     * @return experiments page
     */
    public Page<Experiment> getNextPage(
            @ValidPageRequest(filterTemplateName = EXPERIMENT) PageRequestDto pageRequestDto) {
        log.info("Gets experiments next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortFields(), CREATION_DATE, true);
        List<String> globalFilterFields = filterTemplateService.getGlobalFilterFields(EXPERIMENT);
        ExperimentFilter filter =
                new ExperimentFilter(pageRequestDto.getSearchQuery(), globalFilterFields, pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var experimentsPage = experimentRepository.findAll(filter, pageRequest);
        log.info("Experiments page [{} of {}] with size [{}] has been fetched for page request [{}]",
                experimentsPage.getNumber(), experimentsPage.getTotalPages(), experimentsPage.getNumberOfElements(),
                pageRequestDto);
        return experimentsPage;
    }

    /**
     * Gets experiment by id.
     *
     * @param id - experiment id
     * @return experiment entity
     */
    public Experiment getById(Long id) {
        return experimentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Experiment.class, id));
    }

    /**
     * Calculates experiment statuses counting statistics.
     *
     * @return requests status statistics dto
     */
    public RequestStatusStatisticsDto getRequestStatusesStatistics() {
        log.info("Request get experiments statuses statistics");
        List<RequestStatusStatistics> requestStatusStatistics = experimentRepository.getRequestStatusesStatistics();
        var requestStatusStatisticsDto = calculateRequestStatusesStatistics(requestStatusStatistics);
        log.info("Experiments statuses statistics: {}", requestStatusStatisticsDto);
        return requestStatusStatisticsDto;
    }

    /**
     * Calculates experiments statistics data (distribution diagram by experiment type).
     *
     * @param createdDateFrom - experiment created date from
     * @param createdDateTo   - experiment created date to
     * @return experiments statistics
     */
    public ChartDto getExperimentsStatistics(LocalDate createdDateFrom, LocalDate createdDateTo) {
        log.info("Starting to get experiments statistics data with created date from [{}] to [{}]",
                createdDateFrom, createdDateTo);
        CriteriaQuery<Tuple> criteria = buildExperimentStatisticsDataCriteria(createdDateFrom, createdDateTo);
        var experimentStatisticsMap = entityManager.createQuery(criteria).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, ExperimentType.class).name(),
                        tuple -> tuple.get(1, Long.class), (v1, v2) -> v1, TreeMap::new));
        var chartData = populateExperimentsChartData(experimentStatisticsMap);
        log.info("Experiments statistics data has been fetched with created date from [{}] to [{}]: {}",
                createdDateFrom, createdDateTo, chartData);
        return chartData;
    }

    private CriteriaQuery<Tuple> buildExperimentStatisticsDataCriteria(LocalDate createdDateFrom,
                                                                       LocalDate createdDateTo) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        return buildGroupByStatisticsQuery(builder, Experiment.class, root -> root.get(EXPERIMENT_TYPE), CREATION_DATE,
                createdDateFrom, createdDateTo);
    }

    private ChartDto populateExperimentsChartData(Map<String, Long> statisticsMap) {
        var experimentTypesDictionary = filterTemplateService.getFilterDictionary(FilterDictionaries.EXPERIMENT_TYPE);
        return calculateChartData(experimentTypesDictionary, statisticsMap);
    }

    /**
     * Gets experiment results content url.
     *
     * @param id - experiment id
     * @return s3 content response dto
     */
    public S3ContentResponseDto getExperimentResultsContentUrl(Long id) {
        log.info("Starting to get experiment [{}] results content url", id);
        var experiment = getById(id);
        String contentUrl = objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(experiment.getModelPath())
                        .expirationTime(appProperties.getShortLifeUrlExpirationMinutes())
                        .expirationTimeUnit(TimeUnit.MINUTES)
                        .build()
        );
        log.info("Experiment [{}] results content url has been fetched", id);
        return S3ContentResponseDto.builder()
                .contentUrl(contentUrl)
                .build();
    }

    /**
     * Gets experiment model by id.
     *
     * @param id - experiment id
     * @return experiment model
     */
    public ExperimentModel getExperimentModel(Long id) {
        var experiment = getById(id);
        return experimentMapper.mapToModel(experiment);
    }
}
