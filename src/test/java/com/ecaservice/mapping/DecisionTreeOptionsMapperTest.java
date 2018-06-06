package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.DecisionTreeOptions;
import eca.ensemble.forests.DecisionTreeBuilder;
import eca.ensemble.forests.DecisionTreeType;
import eca.trees.DecisionTreeClassifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link DecisionTreeOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DecisionTreeOptionsMapperTest {

    @Inject
    private DecisionTreeOptionsMapper decisionTreeOptionsMapper;

    @Test
    public void testMapDecisionTree() {
        DecisionTreeOptions decisionTreeOptions = TestHelperUtils.createDecisionTreeOptions();
        DecisionTreeBuilder decisionTreeBuilder = new DecisionTreeBuilder();
        for (DecisionTreeType decisionTreeType : DecisionTreeType.values()) {
            decisionTreeOptions.setDecisionTreeType(decisionTreeType);
            assertThat(decisionTreeOptionsMapper.map(decisionTreeOptions)).isInstanceOf(
                    decisionTreeType.handle(decisionTreeBuilder).getClass());
        }
        DecisionTreeClassifier decisionTreeClassifier = decisionTreeOptionsMapper.map(decisionTreeOptions);
        assertThat(decisionTreeClassifier.getMaxDepth()).isEqualTo(decisionTreeOptions.getMaxDepth());
        assertThat(decisionTreeClassifier.getMinObj()).isEqualTo(decisionTreeOptions.getMinObj());
        assertThat(decisionTreeClassifier.getNumRandomAttr()).isEqualTo(decisionTreeOptions.getNumRandomAttr());
        assertThat(decisionTreeClassifier.getNumRandomSplits()).isEqualTo(decisionTreeOptions.getNumRandomSplits());
        assertThat(decisionTreeClassifier.getSeed()).isEqualTo(decisionTreeOptions.getSeed());
        assertThat(decisionTreeClassifier.getUseBinarySplits()).isTrue();
        assertThat(decisionTreeClassifier.isUseRandomSplits()).isFalse();
        assertThat(decisionTreeClassifier.isRandomTree()).isTrue();

    }
}
