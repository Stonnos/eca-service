package com.ecaservice.load.test.service;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.model.TestDataModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Test data iterator for load test.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class LoadTestDataIterator implements Iterator<TestDataModel> {

    private final LoadTestEntity loadTestEntity;
    private final Random sampleRandom;
    private final Random classifiersRandom;
    private final InstancesConfigService instancesConfigService;
    private final ClassifiersConfigService classifiersConfigService;

    private int iteration;

    @Override
    public boolean hasNext() {
        return iteration < loadTestEntity.getNumRequests();
    }

    @Override
    public TestDataModel next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Resource resource = getNextSample();
        ClassifierOptions classifierOptions = getNextClassifierOptions();
        ++iteration;
        return TestDataModel.builder()
                .dataResource(resource)
                .classifierOptions(classifierOptions)
                .build();
    }

    private Resource getNextSample() {
        int sampleIndex = sampleRandom.nextInt(instancesConfigService.size());
        return instancesConfigService.getConfig(sampleIndex);
    }

    private ClassifierOptions getNextClassifierOptions() {
        int classifierIndex = classifiersRandom.nextInt(classifiersConfigService.size());
        return classifiersConfigService.getConfig(classifierIndex);
    }
}
