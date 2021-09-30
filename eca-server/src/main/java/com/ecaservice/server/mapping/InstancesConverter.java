package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.server.util.InstancesUtils;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import weka.core.Instances;

import java.math.BigInteger;

/**
 * Instances converter.
 *
 * @author Roman Batygin
 */
@Component
public class InstancesConverter {

    /**
     * Converts instances to instances report.
     *
     * @param instances - instances object
     * @return instances report
     */
    @Named("instancesToInstancesReport")
    public InstancesReport convert(Instances instances) {
        InstancesReport instancesReport = new InstancesReport();
        instancesReport.setRelationName(instances.relationName());
        instancesReport.setNumInstances(BigInteger.valueOf(instances.numInstances()));
        instancesReport.setNumAttributes(BigInteger.valueOf(instances.numAttributes()));
        instancesReport.setNumClasses(BigInteger.valueOf(instances.numClasses()));
        instancesReport.setClassName(instances.classAttribute().name());
        String instancesJson = InstancesUtils.toJson(instances);
        instancesReport.setStructure(instancesJson);
        return instancesReport;
    }
}
