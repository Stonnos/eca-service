package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.J48Options;
import eca.trees.J48;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements mapping J48 classifier to its options model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.J48_ORDER)
@Mapper
public abstract class J48Mapper extends AbstractClassifierMapper<J48, J48Options> {

    protected J48Mapper() {
        super(J48.class);
    }
}
