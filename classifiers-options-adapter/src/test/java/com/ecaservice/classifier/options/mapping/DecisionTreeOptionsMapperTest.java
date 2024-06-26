package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import eca.ensemble.forests.DecisionTreeBuilder;
import eca.ensemble.forests.DecisionTreeType;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link DecisionTreeOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({DecisionTreeOptionsMapperImpl.class, DecisionTreeFactory.class})
class DecisionTreeOptionsMapperTest {

    private static final double ALPHA_VALUE = 0.002d;

    @Autowired
    private DecisionTreeOptionsMapper decisionTreeOptionsMapper;

    @Test
    void testMapDecisionTree() {
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

    @Test
    void testMapChaid() {
        DecisionTreeOptions decisionTreeOptions = TestHelperUtils.createDecisionTreeOptions();
        decisionTreeOptions.setDecisionTreeType(DecisionTreeType.CHAID);
        decisionTreeOptions.setAlpha(ALPHA_VALUE);
        DecisionTreeClassifier decisionTreeClassifier = decisionTreeOptionsMapper.map(decisionTreeOptions);
        assertThat(decisionTreeClassifier).isInstanceOf(CHAID.class);
        assertThat(((CHAID) decisionTreeClassifier).getAlpha()).isEqualTo(ALPHA_VALUE);
    }
}
