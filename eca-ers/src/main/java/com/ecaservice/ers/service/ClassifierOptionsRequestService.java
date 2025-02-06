package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.exception.ResultsNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

/**
 * Service for handling classifier options requests.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsRequestService {

    private final ClassifierOptionsService classifierOptionsService;

    /**
     * Finds optimal classifiers options.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    public ClassifierOptionsResponse findClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        log.info("Starting to find optimal classifiers options with request id [{}] for data uuid [{}]",
                classifierOptionsRequest.getRequestId(), classifierOptionsRequest.getDataUuid());
        var classifierOptionsInfoList = classifierOptionsService.findBestClassifierOptions(classifierOptionsRequest);
        if (CollectionUtils.isEmpty(classifierOptionsInfoList)) {
            throw new ResultsNotFoundException(
                    String.format("Best classifiers options not found for data uuid [%s], request id [%s]",
                            classifierOptionsRequest.getDataUuid(), classifierOptionsRequest.getRequestId()));
        } else {
            log.info("[{}] best classifiers options has been found for data uuid [{}], request id [{}]",
                    classifierOptionsInfoList.size(), classifierOptionsRequest.getDataUuid(),
                    classifierOptionsRequest.getRequestId());
            var classifierOptionsReports = classifierOptionsInfoList.stream()
                    .map(evaluationResultsInfo -> {
                        ClassifierReport classifierReport = new ClassifierReport();
                        classifierReport.setClassifierName(evaluationResultsInfo.getClassifierName());
                        classifierReport.setOptions(evaluationResultsInfo.getClassifierOptions());
                        return classifierReport;
                    }).collect(Collectors.toList());
            return ClassifierOptionsResponse.builder()
                    .requestId(classifierOptionsRequest.getRequestId())
                    .classifierReports(classifierOptionsReports)
                    .build();
        }
    }
}
