package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.RandomForestsOptions;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.RandomForests;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link RandomForestsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(RandomForestsMapperImpl.class)
class RandomForestsMapperTest {

    @Inject
    private RandomForestsMapper randomForestsMapper;

    @Test
    void testRandomForests() {
        RandomForests randomForests = TestHelperUtils.createRandomForests(DecisionTreeType.ID3);
        RandomForestsOptions options = randomForestsMapper.map(randomForests);
        Assertions.assertThat(options.getSeed()).isEqualTo(randomForests.getSeed());
        Assertions.assertThat(options.getNumThreads()).isEqualTo(randomForests.getNumThreads());
        Assertions.assertThat(options.getNumIterations()).isEqualTo(randomForests.getNumIterations());
        Assertions.assertThat(options.getMaxDepth()).isEqualTo(randomForests.getMaxDepth());
        Assertions.assertThat(options.getMinObj()).isEqualTo(randomForests.getMinObj());
        Assertions.assertThat(options.getNumRandomAttr()).isEqualTo(randomForests.getNumRandomAttr());
        Assertions.assertThat(options.getDecisionTreeType()).isEqualTo(randomForests.getDecisionTreeType());
    }
}
