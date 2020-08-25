package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.ExtraTreesOptions;
import eca.ensemble.forests.ExtraTreesClassifier;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements Extra trees input options mapping to Extra trees model.
 *
 * @author Roman Batygin
 */
@Mapper
@Order(Ordered.EXTRA_TREES_ORDER)
public abstract class ExtraTreesOptionsMapper extends ClassifierOptionsMapper<ExtraTreesOptions, ExtraTreesClassifier> {

    public ExtraTreesOptionsMapper() {
        super(ExtraTreesOptions.class);
    }
}
