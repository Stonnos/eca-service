package com.ecaservice.converters;

import com.ecaservice.model.options.DecisionTreeOptions;
import eca.ensemble.forests.DecisionTreeBuilder;
import eca.trees.DecisionTreeClassifier;
import org.springframework.stereotype.Component;

/**
 * Implements decision tree options converted.
 *
 * @author Roman Batygin
 */
@Component
public class DecisionTreeOptionsConverter
        extends ClassifierOptionsConverter<DecisionTreeOptions, DecisionTreeClassifier> {

    private static final DecisionTreeBuilder DECISION_TREE_BUILDER = new DecisionTreeBuilder();

    /**
     * Default constructor.
     */
    public DecisionTreeOptionsConverter() {
        super(DecisionTreeOptions.class);
    }

    @Override
    public DecisionTreeClassifier convert(DecisionTreeOptions decisionTreeOptions) {
        DecisionTreeClassifier decisionTreeClassifier =
                decisionTreeOptions.getDecisionTreeType().handle(DECISION_TREE_BUILDER);
        //decisionTreeClassifier.setMinObj(decisionTreeOptions.getNumMinObj());
        decisionTreeClassifier.setMaxDepth(decisionTreeOptions.getMaxDepth());
        decisionTreeClassifier.setUseBinarySplits(decisionTreeOptions.getUseBinarySplits());
        decisionTreeClassifier.setUseRandomSplits(decisionTreeOptions.getUseRandomSplits());
        //decisionTreeClassifier.setNumRandomAttr(decisionTreeOptions.getNumRandomAttrs());
        decisionTreeClassifier.setNumRandomSplits(decisionTreeOptions.getNumRandomSplits());
        return decisionTreeClassifier;
    }
}
