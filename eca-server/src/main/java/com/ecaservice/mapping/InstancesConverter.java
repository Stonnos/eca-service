package com.ecaservice.mapping;

import com.ecaservice.ers.dto.InstancesReport;
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

    private static final InstancesJsonConverter INSTANCES_JSON_CONVERTER = new InstancesJsonConverter();

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
        String instancesJson = INSTANCES_JSON_CONVERTER.convert(instances);
        instancesReport.setStructure(instancesJson);
        return instancesReport;
    }
}
