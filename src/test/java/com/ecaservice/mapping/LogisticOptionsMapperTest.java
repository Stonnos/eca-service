package com.ecaservice.mapping;

import com.ecaservice.model.options.LogisticOptions;
import eca.regression.Logistic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link LogisticOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogisticOptionsMapperTest {

    private static final int MAX_ITS = 500;

    @Inject
    private LogisticOptionsMapper logisticOptionsMapper;

    @Test
    public void testMapLogisticOptions() {
        LogisticOptions logisticOptions = new LogisticOptions();
        logisticOptions.setMaxIts(MAX_ITS);
        logisticOptions.setUseConjugateGradientDescent(true);
        Logistic logistic = logisticOptionsMapper.map(logisticOptions);
        assertThat(logistic.getMaxIts()).isEqualTo(logisticOptions.getMaxIts());
        assertThat(logistic.getUseConjugateGradientDescent()).isTrue();
    }
}
