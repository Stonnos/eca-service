package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.RandomForestsOptions;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.RandomForests;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link RandomForestsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RandomForestsMapperTest {

    @Inject
    private RandomForestsMapper randomForestsMapper;

    @Test
    public void testRandomForests() {
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
