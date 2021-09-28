package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.exception.ResultsNotFoundException;
import com.ecaservice.ers.mapping.ClassifierOptionsInfoMapper;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.UUID;

import static com.ecaservice.ers.config.MetricConstants.GET_OPTIMAL_CLASSIFIER_OPTIONS_TIMED_METRIC_NAME;

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
    private final ClassifierOptionsInfoMapper classifierOptionsInfoMapper;

    /**
     * Finds optimal classifiers options.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    @Timed(value = GET_OPTIMAL_CLASSIFIER_OPTIONS_TIMED_METRIC_NAME)
    public ClassifierOptionsResponse findClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        String requestId = UUID.randomUUID().toString();
        log.info("Received request [{}] for searching optimal classifiers options.", requestId);
        log.info("Starting to find optimal classifiers options with request id [{}] for data '{}' classification.",
                requestId, classifierOptionsRequest.getRelationName());
        var classifierOptionsInfoList = classifierOptionsService.findBestClassifierOptions(classifierOptionsRequest);
        if (CollectionUtils.isEmpty(classifierOptionsInfoList)) {
            throw new ResultsNotFoundException(
                    String.format("Best classifiers options not found for data [%s], request id [%s]",
                            classifierOptionsRequest.getRelationName(), requestId));
        } else {
            log.info("[{}] best classifiers options has been found for data [{}], request id [{}]",
                    classifierOptionsInfoList.size(), classifierOptionsRequest.getRelationName(), requestId);
            var classifierOptionsReports = classifierOptionsInfoMapper.map(classifierOptionsInfoList);
            return ClassifierOptionsResponse.builder()
                    .requestId(requestId)
                    .classifierReports(classifierOptionsReports)
                    .build();
        }
    }
}
