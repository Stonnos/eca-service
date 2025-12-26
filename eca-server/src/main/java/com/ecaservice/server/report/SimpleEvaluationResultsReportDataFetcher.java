package com.ecaservice.server.report;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.report.model.EvaluationResultsReportBean;
import com.ecaservice.server.report.model.EvaluationResultsReportInputData;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.service.EvaluationResultsAttachmentService;
import com.ecaservice.server.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.server.util.EvaluationResultsAttachmentKeys.EVALUATION_KEY_FORMAT;
import static com.ecaservice.server.util.RoutePaths.EVALUATION_RESULTS_DETAILS_PATH;
import static com.ecaservice.server.util.ValidationUtils.checkFinishedRequestStatus;

/**
 * Evaluation results report data fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleEvaluationResultsReportDataFetcher implements EvaluationResultsReportDataFetcher {

    private final AppProperties appProperties;
    private final EvaluationResultsReportDataProcessor evaluationResultsReportDataProcessor;
    private final ErsRequestService ersRequestService;
    private final EvaluationResultsAttachmentService evaluationResultsAttachmentService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;

    /**
     * Gets evaluation results report data.
     *
     * @param id - evaluation log id
     * @return evaluation results report data
     */
    @Override
    public EvaluationResultsReportBean getReportData(long id) {
        log.info("Starting to fetch evaluation result report data for evaluation log [{}]", id);
        EvaluationLog evaluationLog = evaluationLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, id));
        checkFinishedRequestStatus(evaluationLog);
        EvaluationResultsRequestEntity evaluationResultsRequestEntity =
                evaluationResultsRequestEntityRepository.findByEvaluationLog(evaluationLog);
        if (evaluationResultsRequestEntity == null ||
                !ErsResponseStatus.SUCCESS.equals(evaluationResultsRequestEntity.getResponseStatus())) {
            throw new EntityNotFoundException(EvaluationResultsRequestEntity.class, evaluationLog.getId());
        } else {
            GetEvaluationResultsResponse evaluationResultsResponse =
                    ersRequestService.getEvaluationResults(evaluationResultsRequestEntity.getRequestId());
            String rocAttachmentKey = String.format(EVALUATION_KEY_FORMAT, evaluationLog.getRequestId());
            byte[] rocImage = evaluationResultsAttachmentService.getAttachment(rocAttachmentKey,
                    EvaluationResultsAttachmentType.ROC_CURVE_IMAGE);
            String externalDetailsUrl = getExternalDetailsUrl(evaluationLog);
            EvaluationResultsReportInputData evaluationResultsReportInputData =
                    EvaluationResultsReportInputData.builder()
                            .evaluationEntity(evaluationLog)
                            .classifierOptions(evaluationLog.getClassifierOptions())
                            .evaluationResultsResponse(evaluationResultsResponse)
                            .rocImage(rocImage)
                            .externalDetailsUrl(externalDetailsUrl)
                            .build();
            EvaluationResultsReportBean evaluationResultsReportBean =
                    evaluationResultsReportDataProcessor.processReportData(evaluationResultsReportInputData);
            log.info("Evaluation result report data has been fetched for evaluation log [{}]", id);
            return evaluationResultsReportBean;
        }
    }

    private String getExternalDetailsUrl(EvaluationLog evaluationLog) {
        String path = String.format(EVALUATION_RESULTS_DETAILS_PATH, evaluationLog.getId());
        return String.format("%s%s", appProperties.getWebExternalBaseUrl(), path);
    }
}
