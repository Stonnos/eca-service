package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.RandomForestsOptions;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.RandomForests;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link RandomForestsOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(RandomForestsOptionsMapperImpl.class)
public class RandomForestsOptionsMapperTest {

    @Inject
    private RandomForestsOptionsMapper randomForestsOptionsMapper;

    @Test
    void testMapRandomForestsOptions() {
        RandomForestsOptions randomForestsOptions = TestHelperUtils.createRandomForestsOptions(DecisionTreeType.C45);
        RandomForests randomForests = randomForestsOptionsMapper.map(randomForestsOptions);
        Assertions.assertThat(randomForests.getSeed()).isEqualTo(randomForestsOptions.getSeed());
        Assertions.assertThat(randomForests.getNumIterations()).isEqualTo(randomForestsOptions.getNumIterations());
        Assertions.assertThat(randomForests.getNumThreads()).isEqualTo(randomForestsOptions.getNumThreads());
        Assertions.assertThat(randomForests.getDecisionTreeType()).isEqualTo(
                randomForestsOptions.getDecisionTreeType());
        Assertions.assertThat(randomForests.getMaxDepth()).isEqualTo(randomForestsOptions.getMaxDepth());
        Assertions.assertThat(randomForests.getMinObj()).isEqualTo(randomForestsOptions.getMinObj());
        Assertions.assertThat(randomForests.getNumRandomAttr()).isEqualTo(randomForestsOptions.getNumRandomAttr());
    }
}
