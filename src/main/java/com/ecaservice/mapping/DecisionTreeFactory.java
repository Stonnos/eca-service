package com.ecaservice.mapping;

import com.ecaservice.model.options.DecisionTreeOptions;
import eca.ensemble.forests.DecisionTreeBuilder;
import eca.trees.CART;
import eca.trees.DecisionTreeClassifier;
import org.mapstruct.ObjectFactory;
import org.springframework.stereotype.Component;

/**
 * Decision tree factory.
 *
 * @author Roman Batygin
 */
@Component
public class DecisionTreeFactory {

    private static final DecisionTreeBuilder DECISION_TREE_BUILDER = new DecisionTreeBuilder();

    /**
     * Creates decision tree classifier by specified type.
     *
     * @param decisionTreeOptions decision tree options
     * @return decision tree classifier
     */
    @ObjectFactory
    public DecisionTreeClassifier createDecisionTreeClassifier(DecisionTreeOptions decisionTreeOptions) {
        return decisionTreeOptions.getDecisionTreeType() != null ?
                decisionTreeOptions.getDecisionTreeType().handle(DECISION_TREE_BUILDER) : new CART();
    }
}
