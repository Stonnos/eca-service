package com.ecaservice.mapping.options;

import com.ecaservice.model.options.LogisticOptions;
import eca.regression.Logistic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link LogisticMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(LogisticMapperImpl.class)
public class LogisticMapperTest {

    @Inject
    private LogisticMapper logisticMapper;

    @Test
    void testMapLogistic() {
        Logistic logistic = new Logistic();
        LogisticOptions options = logisticMapper.map(logistic);
        Assertions.assertThat(options.getMaxIts()).isEqualTo(logistic.getMaxIts());
        Assertions.assertThat(options.getUseConjugateGradientDescent()).isEqualTo(
                logistic.getUseConjugateGradientDescent());
    }
}
