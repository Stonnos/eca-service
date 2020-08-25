package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.ExtraTreesOptions;
import eca.ensemble.forests.ExtraTreesClassifier;
import org.mapstruct.Mapper;

/**
 * Implements extra trees classifier mapping to its input options.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExtraTreesMapper extends AbstractClassifierMapper<ExtraTreesClassifier, ExtraTreesOptions> {

    protected ExtraTreesMapper() {
        super(ExtraTreesClassifier.class);
    }
}
