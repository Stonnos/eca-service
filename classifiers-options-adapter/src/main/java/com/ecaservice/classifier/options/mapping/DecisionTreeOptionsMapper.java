package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.core.annotation.Order;

/**
 * Implements decision tree input options mapping to decision tree model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.DECISION_TREE_ORDER)
@Mapper(uses = DecisionTreeFactory.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class DecisionTreeOptionsMapper
        extends ClassifierOptionsMapper<DecisionTreeOptions, DecisionTreeClassifier> {

    protected DecisionTreeOptionsMapper() {
        super(DecisionTreeOptions.class);
    }

    @AfterMapping
    protected void mapAdditionalOptions(DecisionTreeOptions options, @MappingTarget DecisionTreeClassifier classifier) {
        if (classifier instanceof CHAID) {
            CHAID chaid = (CHAID) classifier;
            if (options.getAlpha() != null) {
                chaid.setAlpha(options.getAlpha());
            }
        }
    }
}
