package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.classifier.options.model.OptionsVariables;
import eca.ensemble.forests.DecisionTreeType;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import eca.trees.ID3;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Collections;

/**
 * Implements mapping decision tree classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class DecisionTreeMapper extends AbstractClassifierMapper<DecisionTreeClassifier, DecisionTreeOptions> {

    protected DecisionTreeMapper() {
        super(DecisionTreeClassifier.class);
    }

    @AfterMapping
    protected void mapDecisionTreeType(DecisionTreeClassifier classifier, @MappingTarget DecisionTreeOptions options) {
        DecisionTreeType decisionTreeType;
        if (classifier instanceof CART) {
            decisionTreeType = DecisionTreeType.CART;
        } else if (classifier instanceof C45) {
            decisionTreeType = DecisionTreeType.C45;
        } else if (classifier instanceof ID3) {
            decisionTreeType = DecisionTreeType.ID3;
        } else if (classifier instanceof CHAID) {
            decisionTreeType = DecisionTreeType.CHAID;
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported decision tree type: %s!", classifier.getClass().getSimpleName()));
        }
        options.setDecisionTreeType(decisionTreeType);
    }

    @AfterMapping
    protected void mapAdditionalOptions(DecisionTreeClassifier classifier, @MappingTarget DecisionTreeOptions options) {
        if (classifier instanceof CHAID) {
            CHAID chaid = (CHAID) classifier;
            options.setAdditionalOptions(
                    Collections.singletonMap(OptionsVariables.ALPHA, String.valueOf(chaid.getAlpha())));
        }
    }
}
