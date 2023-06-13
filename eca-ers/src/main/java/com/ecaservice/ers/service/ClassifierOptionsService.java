package com.ecaservice.ers.service;

import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.dto.SortDirection;
import com.ecaservice.ers.dto.EvaluationResultsStatisticsField;
import com.ecaservice.ers.exception.DataNotFoundException;
import com.ecaservice.ers.filter.EvaluationResultsFilter;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements service for searching optimal classifier options.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsService {

    private static final Map<EvaluationResultsStatisticsField, String> STATISTICS_FIELD_MAP = Map.of(
            EvaluationResultsStatisticsField.PCT_CORRECT, "statistics.pctCorrect",
            EvaluationResultsStatisticsField.MAX_AUC_VALUE, "statistics.maxAucValue",
            EvaluationResultsStatisticsField.VARIANCE_ERROR, "statistics.varianceError"
    );

    private final InstancesInfoRepository instancesInfoRepository;
    private final EvaluationResultsInfoRepository evaluationResultsInfoRepository;
    private final ErsConfig ersConfig;
    private final SortFieldService sortFieldService;

    /**
     * Finds optimal classifiers options.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return optimal classifiers options list
     */
    public List<ClassifierOptionsInfo> findBestClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        String dataHash = classifierOptionsRequest.getDataHash();
        var instancesInfo = instancesInfoRepository.findByDataMd5Hash(dataHash);
        if (instancesInfo == null) {
            throw new DataNotFoundException(String.format("Instances with md5 hash [%s] doesn't exists!",
                    classifierOptionsRequest.getDataHash()));
        } else {
            log.info("[{}] instances info has been found with data md5 hash [{}]", instancesInfo.getRelationName(),
                    instancesInfo.getDataMd5Hash());
            EvaluationMethodReport evaluationMethodReport = classifierOptionsRequest.getEvaluationMethodReport();
            EvaluationResultsFilter filter = new EvaluationResultsFilter(instancesInfo, evaluationMethodReport);
            Sort sort = buildSort(classifierOptionsRequest);
            PageRequest pageRequest = PageRequest.of(0, ersConfig.getResultSize(), sort);
            var evaluationResultsInfoPage = evaluationResultsInfoRepository.findAll(filter, pageRequest);
            return evaluationResultsInfoPage.getContent()
                    .stream()
                    .map(EvaluationResultsInfo::getClassifierOptionsInfo)
                    .collect(Collectors.toList());
        }
    }

    private Sort buildSort(ClassifierOptionsRequest classifierOptionsRequest) {
        if (CollectionUtils.isEmpty(classifierOptionsRequest.getEvaluationResultsStatisticsSortFields())) {
            return sortFieldService.getEvaluationResultsDefaultSort();
        } else {
            Sort.Order[] orders = classifierOptionsRequest.getEvaluationResultsStatisticsSortFields()
                    .stream()
                    .map(sortField -> {
                        String fieldName = STATISTICS_FIELD_MAP.get(sortField.getField());
                        return SortDirection.DESC.equals(sortField.getDirection()) ? Sort.Order.desc(fieldName) :
                                Sort.Order.asc(fieldName);
                    })
                    .toArray(Sort.Order[]::new);
            return Sort.by(orders);
        }
    }
}
