package com.ecaservice.service.experiment.handler;

import eca.trees.CART;
import eca.trees.DecisionTreeClassifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link DecisionTreeInputDataHandler} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(DecisionTreeInputDataHandler.class)
public class DecisionTreeInputDataHandlerTest {

    private static final int DEFAULT_NUM_ATTRIBUTES = 10;

    @Autowired
    private DecisionTreeInputDataHandler decisionTreeInputDataHandler;

    @Mock
    private Instances data;

    /**
     * Test checking following cases:
     * Case 1: Nullable input data -> 0
     * Case 2: Decision tree isn't random -> 0
     * Case 3: Decision tree is random and random attributes number is 0 -> not 0
     * Case 4: Decision tree is random and random attributes number is grater than data attributes number -> not 0
     * Case 5: Decision tree is random and random attributes number is less than data attributes number -> not 0
     */
    @Test
    public void testDecisionTreeInputDataHandle() {
        DecisionTreeClassifier classifier = new CART();
        classifier.setNumRandomAttr(0);
        classifier.setRandomTree(false);
        //Case 1
        decisionTreeInputDataHandler.handle(null, classifier);
        assertThat(classifier.numRandomAttr()).isZero();
        //Case 2
        decisionTreeInputDataHandler.handle(data, classifier);
        assertThat(classifier.numRandomAttr()).isZero();
        //Case 3
        classifier.setRandomTree(true);
        when(data.numAttributes()).thenReturn(DEFAULT_NUM_ATTRIBUTES);
        decisionTreeInputDataHandler.handle(data, classifier);
        assertThat(classifier.numRandomAttr()).isNotZero();
        //Case 4
        classifier.setNumRandomAttr(DEFAULT_NUM_ATTRIBUTES + 1);
        decisionTreeInputDataHandler.handle(data, classifier);
        assertThat(classifier.numRandomAttr()).isNotZero();
        //Case 5
        classifier.setNumRandomAttr(DEFAULT_NUM_ATTRIBUTES - 1);
        assertThat(classifier.numRandomAttr()).isEqualTo(DEFAULT_NUM_ATTRIBUTES - 1);
    }
}
