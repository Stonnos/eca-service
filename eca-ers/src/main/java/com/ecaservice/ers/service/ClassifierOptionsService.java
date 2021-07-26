package com.ecaservice.ers.service;

import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.dto.SortDirection;
import com.ecaservice.ers.exception.DataNotFoundException;
import com.ecaservice.ers.filter.EvaluationResultsFilter;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
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
        Long instancesInfoId = instancesInfoRepository.findIdByDataMd5Hash(dataHash);
        if (instancesInfoId == null) {
            throw new DataNotFoundException(
                    String.format("Instances '%s' doesn't exists!", classifierOptionsRequest.getRelationName()));
        } else {
            EvaluationMethodReport evaluationMethodReport = classifierOptionsRequest.getEvaluationMethodReport();
            EvaluationResultsFilter filter = new EvaluationResultsFilter(instancesInfoId, evaluationMethodReport);
            Sort sort = buildSort(classifierOptionsRequest);
            PageRequest pageRequest = PageRequest.of(0, ersConfig.getResultSize(), sort);
            Page<EvaluationResultsInfo> evaluationResultsInfoPage =
                    evaluationResultsInfoRepository.findAll(filter, pageRequest);
            return evaluationResultsInfoPage.getContent().stream()
                    .map(EvaluationResultsInfo::getClassifierOptionsInfo)
                    .collect(Collectors.toList());
        }
    }

    private Sort buildSort(ClassifierOptionsRequest classifierOptionsRequest) {
        if (CollectionUtils.isEmpty(classifierOptionsRequest.getSortFields())) {
            return sortFieldService.getEvaluationResultsDefaultSort();
        } else {
            Sort.Order[] orders = classifierOptionsRequest.getSortFields().stream()
                    .map(sortField -> SortDirection.DESC.equals(sortField.getDirection()) ?
                            Sort.Order.desc(sortField.getFieldName()) : Sort.Order.asc(sortField.getFieldName())
                    ).toArray(Sort.Order[]::new);
            return Sort.by(orders);
        }
    }
}
