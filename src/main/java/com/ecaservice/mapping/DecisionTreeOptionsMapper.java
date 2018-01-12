package com.ecaservice.mapping;

import com.ecaservice.model.options.DecisionTreeOptions;
import eca.trees.DecisionTreeClassifier;
import org.mapstruct.Mapper;

/**
 * Implements decision tree input options mapping to decision tree model.
 *
 * @author Roman Batygin
 */
@Mapper(uses = DecisionTreeFactory.class)
public abstract class DecisionTreeOptionsMapper
        extends ClassifierOptionsMapper<DecisionTreeOptions, DecisionTreeClassifier> {

    protected DecisionTreeOptionsMapper() {
        super(DecisionTreeOptions.class);
    }
}
