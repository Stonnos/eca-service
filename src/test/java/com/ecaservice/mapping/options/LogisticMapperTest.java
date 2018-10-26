package com.ecaservice.mapping.options;

import com.ecaservice.model.options.LogisticOptions;
import eca.regression.Logistic;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link LogisticMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(LogisticMapperImpl.class)
public class LogisticMapperTest {

    @Inject
    private LogisticMapper logisticMapper;

    @Test
    public void testMapLogistic() {
        Logistic logistic = new Logistic();
        LogisticOptions options = logisticMapper.map(logistic);
        Assertions.assertThat(options.getMaxIts()).isEqualTo(logistic.getMaxIts());
        Assertions.assertThat(options.getUseConjugateGradientDescent()).isEqualTo(
                logistic.getUseConjugateGradientDescent());
    }
}
