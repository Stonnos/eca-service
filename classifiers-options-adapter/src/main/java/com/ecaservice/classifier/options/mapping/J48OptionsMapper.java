package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.J48Options;
import eca.trees.J48;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements J48 input options mapping to J48 model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.J48_ORDER)
@Mapper
public abstract class J48OptionsMapper extends ClassifierOptionsMapper<J48Options, J48> {

    protected J48OptionsMapper() {
        super(J48Options.class);
    }
}
