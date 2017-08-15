package com.ecaservice.service.impl;

import com.ecaservice.config.DataBaseConfig;
import com.ecaservice.mapping.OrikaBeanMapper;
import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationOptions;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.EvaluationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author Roman Batygin
 */
@Service
public class EvaluationLogServiceImpl implements EvaluationLogService {

    private EvaluationLogRepository evaluationLogRepository;
    private DataBaseConfig dataBaseConfig;
    private OrikaBeanMapper mapper;

    /**
     * Constructor with dependency spring injection.
     *
     * @param evaluationLogRepository {@link EvaluationLogRepository} bean
     * @param dataBaseConfig          {@link DataBaseConfig} bean
     * @param mapper                  {@link OrikaBeanMapper} bean
     */
    @Autowired
    public EvaluationLogServiceImpl(EvaluationLogRepository evaluationLogRepository,
                                    DataBaseConfig dataBaseConfig,
                                    OrikaBeanMapper mapper) {
        this.evaluationLogRepository = evaluationLogRepository;
        this.dataBaseConfig = dataBaseConfig;
        this.mapper = mapper;

    }

    @Override
    @Transactional
    public void save(ClassificationResult result, EvaluationMethod evaluationMethod,
                     Integer numFolds, Integer numTests, LocalDateTime requestDate, String ipAddress) {

        if (Boolean.TRUE.equals(dataBaseConfig.getEnabled())) {
            EvaluationLog evaluationLog = mapper.map(result, EvaluationLog.class);
            evaluationLog.setIpAddress(ipAddress);
            evaluationLog.setRequestDate(requestDate);
            evaluationLog.setEvaluationMethod(evaluationMethod);
            evaluationLog.setEvaluationOptions(new EvaluationOptions(numFolds, numTests));
            evaluationLogRepository.save(evaluationLog);
        }

    }
}
