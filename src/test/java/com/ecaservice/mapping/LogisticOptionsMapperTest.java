package com.ecaservice.mapping;

import com.ecaservice.model.options.LogisticOptions;
import eca.regression.Logistic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link LogisticOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(LogisticOptionsMapperImpl.class)
public class LogisticOptionsMapperTest {

    @Autowired
    private LogisticOptionsMapper logisticOptionsMapper;

    @Test
    public void testMapLogisticOptions() {
        LogisticOptions logisticOptions = new LogisticOptions();
        logisticOptions.setMaxIts(500);
        logisticOptions.setUseConjugateGradientDescent(true);
        Logistic logistic = logisticOptionsMapper.map(logisticOptions);
        assertThat(logistic.getMaxIts()).isEqualTo(logisticOptions.getMaxIts());
        assertThat(logistic.getUseConjugateGradientDescent()).isTrue();
    }
}
