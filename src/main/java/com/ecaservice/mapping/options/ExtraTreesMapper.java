package com.ecaservice.mapping.options;

import com.ecaservice.mapping.options.AbstractClassifierMapper;
import com.ecaservice.model.options.ExtraTreesOptions;
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
