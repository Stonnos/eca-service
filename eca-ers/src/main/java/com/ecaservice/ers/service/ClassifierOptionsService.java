package com.ecaservice.ers.service;

import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.exception.DataNotFoundException;
import com.ecaservice.ers.filter.OptimalEvaluationResultsFilter;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    @NewSpan
    public List<ClassifierOptionsInfo> findBestClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        String dataUuid = classifierOptionsRequest.getDataUuid();
        var instancesInfo = instancesInfoRepository.findByUuid(dataUuid);
        if (instancesInfo == null) {
            throw new DataNotFoundException(String.format("Instances with uuid [%s] doesn't exists!",
                    classifierOptionsRequest.getDataUuid()));
        } else {
            log.info("[{}] instances info has been found with uuid [{}]", instancesInfo.getRelationName(),
                    instancesInfo.getUuid());
            EvaluationMethodReport evaluationMethodReport = classifierOptionsRequest.getEvaluationMethodReport();
            var filter = new OptimalEvaluationResultsFilter(instancesInfo, evaluationMethodReport);
            Sort sort = sortFieldService.buildSort(classifierOptionsRequest);
            PageRequest pageRequest = PageRequest.of(0, ersConfig.getResultSize(), sort);
            var evaluationResultsInfoPage = evaluationResultsInfoRepository.findAll(filter, pageRequest);
            return evaluationResultsInfoPage.getContent()
                    .stream()
                    .map(EvaluationResultsInfo::getClassifierInfo)
                    .collect(Collectors.toList());
        }
    }
}
