package com.ecaservice.mapping.options;

import com.ecaservice.model.options.ExtraTreesOptions;
import eca.ensemble.forests.ExtraTreesClassifier;
import org.mapstruct.Mapper;

/**
 * Implements Extra trees input options mapping to Extra trees model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExtraTreesOptionsMapper extends ClassifierOptionsMapper<ExtraTreesOptions, ExtraTreesClassifier> {

    public ExtraTreesOptionsMapper() {
        super(ExtraTreesOptions.class);
    }
}
