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

    /**
     * Creates decision tree classifier by specified type.
     *
     * @param decisionTreeOptions decision tree options
     * @return decision tree classifier
     */
    @ObjectFactory
    public DecisionTreeClassifier createDecisionTreeClassifier(DecisionTreeOptions decisionTreeOptions) {
        return decisionTreeOptions.getDecisionTreeType() != null ?
                decisionTreeOptions.getDecisionTreeType().handle(new DecisionTreeBuilder()) : new CART();
    }
}
