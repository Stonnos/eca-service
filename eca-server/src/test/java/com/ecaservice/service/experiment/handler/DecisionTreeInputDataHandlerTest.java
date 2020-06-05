package com.ecaservice.service.experiment.handler;

import eca.trees.CART;
import eca.trees.DecisionTreeClassifier;
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
 * Unit tests for checking {@link DecisionTreeInputDataHandler} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(DecisionTreeInputDataHandler.class)
class DecisionTreeInputDataHandlerTest {

    private static final int DEFAULT_NUM_ATTRIBUTES = 10;

    @Inject
    private DecisionTreeInputDataHandler decisionTreeInputDataHandler;

    @Mock
    private Instances data;

    /**
     * Test checking following cases:
     * Case 1: Decision tree isn't random -> 0
     * Case 2: Decision tree is random and random attributes number is 0 -> not 0
     * Case 3: Decision tree is random and random attributes number is grater than data attributes number -> not 0
     * Case 4: Decision tree is random and random attributes number is less than data attributes number -> not 0
     */
    @Test
    void testDecisionTreeInputDataHandle() {
        DecisionTreeClassifier classifier = new CART();
        classifier.setNumRandomAttr(0);
        classifier.setRandomTree(false);
        //Case 1
        decisionTreeInputDataHandler.handle(data, classifier);
        assertThat(classifier.getNumRandomAttr()).isZero();
        //Case 2
        classifier.setRandomTree(true);
        when(data.numAttributes()).thenReturn(DEFAULT_NUM_ATTRIBUTES);
        decisionTreeInputDataHandler.handle(data, classifier);
        assertThat(classifier.getNumRandomAttr()).isNotZero();
        //Case 3
        classifier.setNumRandomAttr(DEFAULT_NUM_ATTRIBUTES + 1);
        decisionTreeInputDataHandler.handle(data, classifier);
        assertThat(classifier.getNumRandomAttr()).isNotZero();
        //Case 4
        classifier.setNumRandomAttr(DEFAULT_NUM_ATTRIBUTES - 1);
        assertThat(classifier.getNumRandomAttr()).isEqualTo(DEFAULT_NUM_ATTRIBUTES - 1);
    }
}
