package com.ecaservice.load.test.service;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.mapping.EvaluationRequestMapper;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;

/**
 * Load tests executor.
 *
 * @author Roman Batygin
 */
@Service
public class LoadTestExecutor extends AbstractTestExecutor {

    private Random sampleRandom;
    private Random classifiersRandom;

    private final ClassifiersConfigService classifiersConfigService;
    private final InstancesConfigService instancesConfigService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ecaLoadTestsConfig          - eca load tests config bean
     * @param rabbitSender                - rabbit sender bean
     * @param classifierOptionsAdapter    - classifier options adapter bean
     * @param instancesLoader             - instances loader bean
     * @param evaluationRequestMapper     - evaluation request mapper bean
     * @param loadTestRepository          - load test repository bean
     * @param evaluationRequestRepository - evaluation request repository bean
     * @param classifiersConfigService    - classifiers options service bean
     * @param instancesConfigService      - instances config service bean
     */
    public LoadTestExecutor(EcaLoadTestsConfig ecaLoadTestsConfig,
                            RabbitSender rabbitSender,
                            ClassifierOptionsAdapter classifierOptionsAdapter,
                            InstancesLoader instancesLoader,
                            EvaluationRequestMapper evaluationRequestMapper,
                            LoadTestRepository loadTestRepository,
                            EvaluationRequestRepository evaluationRequestRepository,
                            ClassifiersConfigService classifiersConfigService,
                            InstancesConfigService instancesConfigService) {
        super(ecaLoadTestsConfig, rabbitSender, classifierOptionsAdapter, instancesLoader, evaluationRequestMapper,
                loadTestRepository, evaluationRequestRepository);
        this.classifiersConfigService = classifiersConfigService;
        this.instancesConfigService = instancesConfigService;
    }

    /**
     * Initialize load tests options.
     */
    @PostConstruct
    public void initialize() {
        sampleRandom = new Random(ecaLoadTestsConfig.getSeed());
        classifiersRandom = new Random(ecaLoadTestsConfig.getSeed());
    }

    @Override
    protected Resource getNextSample() {
        int sampleIndex = sampleRandom.nextInt(instancesConfigService.size());
        return instancesConfigService.getConfig(sampleIndex);
    }

    @Override
    protected ClassifierOptions getNextClassifierOptions() {
        int classifierIndex = classifiersRandom.nextInt(classifiersConfigService.size());
        return classifiersConfigService.getConfig(classifierIndex);
    }
}
