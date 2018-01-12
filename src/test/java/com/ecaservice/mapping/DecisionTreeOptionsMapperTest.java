package com.ecaservice.mapping;

import com.ecaservice.model.options.DecisionTreeOptions;
import eca.ensemble.forests.DecisionTreeBuilder;
import eca.ensemble.forests.DecisionTreeType;
import eca.trees.DecisionTreeClassifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link DecisionTreeOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import( {DecisionTreeOptionsMapperImpl.class, DecisionTreeFactory.class})
public class DecisionTreeOptionsMapperTest {

    @Autowired
    private DecisionTreeOptionsMapper decisionTreeOptionsMapper;

    @Test
    public void testMapDecisionTree() {
        DecisionTreeOptions decisionTreeOptions = new DecisionTreeOptions();
        DecisionTreeBuilder decisionTreeBuilder = new DecisionTreeBuilder();
        for (DecisionTreeType decisionTreeType : DecisionTreeType.values()) {
            decisionTreeOptions.setDecisionTreeType(decisionTreeType);
            assertThat(decisionTreeOptionsMapper.map(decisionTreeOptions)).isInstanceOf(
                    decisionTreeType.handle(decisionTreeBuilder).getClass());
        }
        decisionTreeOptions.setMaxDepth(25);
        decisionTreeOptions.setMinObj(4);
        decisionTreeOptions.setNumRandomAttr(1);
        decisionTreeOptions.setNumRandomSplits(22);
        decisionTreeOptions.setRandomTree(true);
        decisionTreeOptions.setUseBinarySplits(true);
        decisionTreeOptions.setUseRandomSplits(false);
        DecisionTreeClassifier decisionTreeClassifier = decisionTreeOptionsMapper.map(decisionTreeOptions);
        assertThat(decisionTreeClassifier.getMaxDepth()).isEqualTo(decisionTreeOptions.getMaxDepth());
        assertThat(decisionTreeClassifier.getMinObj()).isEqualTo(decisionTreeOptions.getMinObj());
        assertThat(decisionTreeClassifier.numRandomAttr()).isEqualTo(decisionTreeOptions.getNumRandomAttr());
        assertThat(decisionTreeClassifier.getNumRandomSplits()).isEqualTo(decisionTreeOptions.getNumRandomSplits());
        assertThat(decisionTreeClassifier.getUseBinarySplits()).isTrue();
        assertThat(decisionTreeClassifier.isUseRandomSplits()).isFalse();
        assertThat(decisionTreeClassifier.isRandomTree()).isTrue();

    }
}
