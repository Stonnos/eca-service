package com.ecaservice.service;

import com.ecaservice.config.DataBaseConfig;
import com.ecaservice.converter.OrikaBeanMapper;
import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationStatus;
import com.ecaservice.model.entity.InputOptions;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.repository.EvaluationLogRepository;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.Date;
import java.util.List;

/**
 * @author Roman Batygin
 */
@Service
public class EcaServiceImpl implements EcaService {

    private EvaluationLogRepository evaluationLogRepository;

    private EvaluationService evaluationService;

    private DataBaseConfig dataBaseConfig;

    private OrikaBeanMapper mapper;

    @Autowired
    public EcaServiceImpl(EvaluationLogRepository evaluationLogRepository,
                          EvaluationService evaluationService,
                          DataBaseConfig dataBaseConfig,
                          OrikaBeanMapper mapper) {
        this.evaluationLogRepository = evaluationLogRepository;
        this.evaluationService = evaluationService;
        this.dataBaseConfig = dataBaseConfig;
        this.mapper = mapper;

    }

    @Override
    @Transactional
    public ClassificationResult execute(AbstractClassifier classifier,
                                        Instances data,
                                        EvaluationMethod evaluationMethod,
                                        Integer numFolds,
                                        Integer numTests) {

        ClassificationResult result =
                evaluationService.evaluateModel(classifier, data, evaluationMethod, numFolds, numTests);

        if (Boolean.TRUE.equals(dataBaseConfig.getEnabled())) {
            EvaluationLog evaluationLog = new EvaluationLog();
            evaluationLog.setClassifierName(classifier.getClass().getSimpleName());
            evaluationLog.setEvaluationMethod(evaluationMethod);

            Type<String[]> arrayType = new TypeBuilder<String[]>() {} .build();
            Type<List<InputOptions>> listType = new TypeBuilder<List<InputOptions>>() {} .build();

            evaluationLog.setInputOptionsList(mapper.map(classifier.getOptions(), arrayType, listType));

            evaluationLog.setInstancesInfo(mapper.map(data, InstancesInfo.class));

            evaluationLog.setEvaluationStatus(result.isSuccess() ? EvaluationStatus.SUCCESS : EvaluationStatus.ERROR);
            evaluationLog.setEvaluationDate(new Date());

            evaluationLogRepository.save(evaluationLog);
        }

        return result;
    }
}
