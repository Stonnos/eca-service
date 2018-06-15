package com.ecaservice.mapping.options;

import com.ecaservice.model.options.J48Options;
import eca.trees.J48;
import org.mapstruct.Mapper;

/**
 * Implements J48 input options mapping to J48 model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class J48OptionsMapper extends ClassifierOptionsMapper<J48Options, J48> {

    protected J48OptionsMapper() {
        super(J48Options.class);
    }
}
