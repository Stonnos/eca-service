package com.ecaservice.mapping;

import com.ecaservice.model.InstancesInfo;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;
import weka.core.Instances;

/**
 * Implements the conversion of input data options into the entity model.
 *
 * @author Roman Batygin
 */
@Component
public class InstancesToInstancesInfoConverter extends CustomConverter<Instances, InstancesInfo> {

    @Override
    public InstancesInfo convert(Instances data, Type<? extends InstancesInfo> instancesType) {
        return new InstancesInfo(data.relationName(), data.numInstances(),
                data.numAttributes(), data.numClasses());
    }
}
