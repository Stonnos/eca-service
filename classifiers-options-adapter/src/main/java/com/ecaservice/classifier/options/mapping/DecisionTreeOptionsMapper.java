package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.classifier.options.model.OptionsVariables;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
                log.debug("CHAID alpha value has been mapped from alpha field");
            } else if (!CollectionUtils.isEmpty(options.getAdditionalOptions())) {
                //Get alpha value from map (for backward compatibility)
                String alphaStr = options.getAdditionalOptions().get(OptionsVariables.ALPHA);
                if (!NumberUtils.isCreatable(alphaStr)) {
                    log.warn("Can't set CHAID alpha. Alpha value [{}] isn't numeric", alphaStr);
                } else {
                    ((CHAID) classifier).setAlpha(Double.parseDouble(alphaStr));
                    log.debug("CHAID alpha value has been mapped from additionalOptions map");
                }
            }
        }
    }
}
