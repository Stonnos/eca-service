package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.LogisticOptions;
import eca.regression.Logistic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link LogisticOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(LogisticOptionsMapperImpl.class)
class LogisticOptionsMapperTest {

    private static final int MAX_ITS = 500;

    @Autowired
    private LogisticOptionsMapper logisticOptionsMapper;

    @Test
    void testMapLogisticOptions() {
        LogisticOptions logisticOptions = new LogisticOptions();
        logisticOptions.setMaxIts(MAX_ITS);
        logisticOptions.setUseConjugateGradientDescent(true);
        Logistic logistic = logisticOptionsMapper.map(logisticOptions);
        assertThat(logistic.getMaxIts()).isEqualTo(logisticOptions.getMaxIts());
        assertThat(logistic.getUseConjugateGradientDescent()).isTrue();
    }
}
