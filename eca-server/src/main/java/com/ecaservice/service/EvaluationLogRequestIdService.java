package com.ecaservice.service;

import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.repository.EvaluationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationLogRequestIdService {

    private final EvaluationLogRepository evaluationLogRepository;

    @Inject
    public EvaluationLogRequestIdService(EvaluationLogRepository evaluationLogRepository) {
        this.evaluationLogRepository = evaluationLogRepository;
    }

    @PostConstruct
    public void perform() {
        List<EvaluationLog> evaluationLogs = evaluationLogRepository.findAllByRequestIdIsNull();
        log.info("Found {} evaluation logs with null request id", evaluationLogs.size());
        if (!CollectionUtils.isEmpty(evaluationLogs)) {
            evaluationLogs.forEach(evaluationLog -> {
                String uuid = UUID.randomUUID().toString();
                if (evaluationLogRepository.existsByRequestId(uuid)) {
                    throw new EcaServiceException(String.format("Evaluation log with request id [%s] is already exists", uuid));
                }
                evaluationLog.setRequestId(uuid);
                evaluationLogRepository.save(evaluationLog);
            });
        }
    }
}
