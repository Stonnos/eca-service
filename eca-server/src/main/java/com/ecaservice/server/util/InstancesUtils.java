package com.ecaservice.server.util;

import com.ecaservice.server.exception.EcaServiceException;
import eca.filter.ConstantAttributesFilter;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

/**
 * Instances utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class InstancesUtils {

    /**
     * Removes constant attributes from specified instances.
     *
     * @param instances - instances object
     * @return result - instances object
     */
    public static Instances removeConstantAttributes(Instances instances) {
        try {
            ConstantAttributesFilter filter = new ConstantAttributesFilter();
            return filter.filterInstances(instances);
        } catch (Exception ex) {
            throw new EcaServiceException(ex.getMessage());
        }
    }
}
