package com.ecaservice.server.service.evaluation.initializers;

import eca.trees.CART;
import eca.trees.DecisionTreeClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.core.Instances;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link DecisionTreeInitializer} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(DecisionTreeInitializer.class)
class DecisionTreeInitializerTest {

    private static final int DEFAULT_NUM_ATTRIBUTES = 10;

    @Inject
    private DecisionTreeInitializer decisionTreeInitializer;

    @Mock
    private Instances data;

    @BeforeEach
    void init() {
        when(data.numAttributes()).thenReturn(DEFAULT_NUM_ATTRIBUTES);
    }

    @Test
    void testInitializeDecisionTreeWithNumRandomAttrGreaterThanDataAttributes() {
        DecisionTreeClassifier classifier = new CART();
        classifier.setNumRandomAttr(DEFAULT_NUM_ATTRIBUTES + 1);
        classifier.setRandomTree(true);
        decisionTreeInitializer.handle(data, classifier);
        assertThat(classifier.getNumRandomAttr()).isZero();
    }

    @Test
    void testInitializeDecisionTreeWithNumRandomAttrLessThanDataAttributes() {
        DecisionTreeClassifier classifier = new CART();
        classifier.setNumRandomAttr(DEFAULT_NUM_ATTRIBUTES - 2);
        classifier.setRandomTree(true);
        decisionTreeInitializer.handle(data, classifier);
        assertThat(classifier.getNumRandomAttr()).isEqualTo(classifier.getNumRandomAttr());
    }

    @Test
    void testInitializeDecisionTreeWithSetUseBinarySplits() {
        DecisionTreeClassifier classifier = new CART();
        classifier.setUseRandomSplits(true);
        decisionTreeInitializer.handle(data, classifier);
        assertThat(classifier.getUseBinarySplits()).isTrue();
    }
}
