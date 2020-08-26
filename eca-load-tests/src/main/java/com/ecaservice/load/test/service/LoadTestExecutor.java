package com.ecaservice.load.test.service;

import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.mapping.EvaluationRequestMapper;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Load tests executor.
 *
 * @author Roman Batygin
 */
@Service
public class LoadTestExecutor extends AbstractTestExecutor {

    /**
     * Constructor with spring dependency injection.
     *
     * @param ecaLoadTestsConfig          - eca load tests config bean
     * @param rabbitSender                - rabbit sender bean
     * @param evaluationRequestMapper     - evaluation request mapper bean
     * @param loadTestRepository          - load test repository bean
     * @param evaluationRequestRepository - evaluation request repository bean
     */
    public LoadTestExecutor(EcaLoadTestsConfig ecaLoadTestsConfig,
                            RabbitSender rabbitSender,
                            EvaluationRequestMapper evaluationRequestMapper,
                            LoadTestRepository loadTestRepository,
                            EvaluationRequestRepository evaluationRequestRepository) {
        super(ecaLoadTestsConfig, rabbitSender, evaluationRequestMapper, loadTestRepository,
                evaluationRequestRepository);
    }

    @Override
    protected Instances getNextInstances() {
        return null;
    }

    @Override
    protected AbstractClassifier getNextClassifier() {
        return null;
    }
}
