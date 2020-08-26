package com.ecaservice.load.test.service;

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
     * @param rabbitSender                - rabbit sender bean
     * @param loadTestRepository          - load test repository bean
     * @param evaluationRequestRepository - evaluation request repository bean
     */
    public LoadTestExecutor(RabbitSender rabbitSender,
                            LoadTestRepository loadTestRepository,
                            EvaluationRequestRepository evaluationRequestRepository) {
        super(rabbitSender, loadTestRepository, evaluationRequestRepository);
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
