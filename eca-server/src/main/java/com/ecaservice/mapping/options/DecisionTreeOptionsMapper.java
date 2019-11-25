package com.ecaservice.mapping.options;

import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.OptionsVariables;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

/**
 * Implements decision tree input options mapping to decision tree model.
 *
 * @author Roman Batygin
 */
@Mapper(uses = DecisionTreeFactory.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class DecisionTreeOptionsMapper
        extends ClassifierOptionsMapper<DecisionTreeOptions, DecisionTreeClassifier> {

    protected DecisionTreeOptionsMapper() {
        super(DecisionTreeOptions.class);
    }

    @AfterMapping
    protected void mapChaid(DecisionTreeOptions options, @MappingTarget DecisionTreeClassifier classifier) {
        if (classifier instanceof CHAID && !CollectionUtils.isEmpty(options.getAdditionalOptions())) {
            String alphaStr = options.getAdditionalOptions().get(OptionsVariables.ALPHA);
            if (NumberUtils.isCreatable(alphaStr)) {
                ((CHAID) classifier).setAlpha(Double.valueOf(alphaStr));
            }
        }
    }
}
