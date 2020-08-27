package com.ecaservice.load.test.service;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.model.TestDataModel;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Random;

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
     * @param classifiersConfigService    - classifiers options service bean
     * @param instancesConfigService      - instances config service bean
     * @param rabbitSender                - rabbit sender bean
     * @param classifierOptionsAdapter    - classifier options adapter bean
     * @param instancesLoader             - instances loader bean
     * @param loadTestMapper              - load test mapper bean
     * @param loadTestRepository          - load test repository bean
     * @param evaluationRequestRepository - evaluation request repository bean
     */
    public LoadTestExecutor(EcaLoadTestsConfig ecaLoadTestsConfig,
                            InstancesConfigService instancesConfigService,
                            ClassifiersConfigService classifiersConfigService,
                            RabbitSender rabbitSender,
                            ClassifierOptionsAdapter classifierOptionsAdapter,
                            InstancesLoader instancesLoader,
                            LoadTestMapper loadTestMapper,
                            LoadTestRepository loadTestRepository,
                            EvaluationRequestRepository evaluationRequestRepository) {
        super(ecaLoadTestsConfig, instancesConfigService, classifiersConfigService, rabbitSender,
                classifierOptionsAdapter, instancesLoader, loadTestMapper, loadTestRepository,
                evaluationRequestRepository);
    }

    @Override
    protected Iterator<TestDataModel> testDataIterator(LoadTestEntity loadTestEntity,
                                                       InstancesConfigService instancesConfigService,
                                                       ClassifiersConfigService classifiersConfigService) {
        Random sampleRandom = new Random(ecaLoadTestsConfig.getSeed());
        Random classifiersRandom = new Random(ecaLoadTestsConfig.getSeed());
        return new LoadTestDataIterator(loadTestEntity, sampleRandom, classifiersRandom, instancesConfigService,
                classifiersConfigService);
    }
}
