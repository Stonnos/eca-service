package com.ecaservice.load.test.service;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.mapping.EvaluationRequestMapper;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import javax.annotation.PostConstruct;
import java.util.List;
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

    private final ClassifiersOptionsService classifiersOptionsService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ecaLoadTestsConfig          - eca load tests config bean
     * @param rabbitSender                - rabbit sender bean
     * @param classifierOptionsAdapter    - classifier options adapter bean
     * @param evaluationRequestMapper     - evaluation request mapper bean
     * @param loadTestRepository          - load test repository bean
     * @param evaluationRequestRepository - evaluation request repository bean
     * @param classifiersOptionsService   - classifiers options service bean
     */
    public LoadTestExecutor(EcaLoadTestsConfig ecaLoadTestsConfig,
                            RabbitSender rabbitSender,
                            ClassifierOptionsAdapter classifierOptionsAdapter,
                            EvaluationRequestMapper evaluationRequestMapper,
                            LoadTestRepository loadTestRepository,
                            EvaluationRequestRepository evaluationRequestRepository,
                            ClassifiersOptionsService classifiersOptionsService) {
        super(ecaLoadTestsConfig, rabbitSender, classifierOptionsAdapter, evaluationRequestMapper, loadTestRepository,
                evaluationRequestRepository);
        this.classifiersOptionsService = classifiersOptionsService;
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
    protected Instances getNextInstances() {
        return null;
    }

    @Override
    protected ClassifierOptions getNextClassifier() {
        List<ClassifierOptions> classifierOptions = classifiersOptionsService.getClassifierOptionsList();
        int classifierIndex = sampleRandom.nextInt(classifierOptions.size());
        return classifierOptions.get(classifierIndex);
    }
}
