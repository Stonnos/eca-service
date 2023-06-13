package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.ers.model.InstancesInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.ers.TestHelperUtils.buildInstancesInfo;
import static com.ecaservice.ers.TestHelperUtils.buildInstancesReport;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link InstancesMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(InstancesMapperImpl.class)
class InstancesMapperTest {

    @Inject
    private InstancesMapper instancesMapper;

    @Test
    void testMapInstancesReport() {
        InstancesReport report = buildInstancesReport();
        InstancesInfo instancesInfo = instancesMapper.map(report);
        assertThat(instancesInfo.getRelationName()).isEqualTo(report.getRelationName());
        assertThat(instancesInfo.getNumClasses().intValue()).isEqualTo(report.getNumClasses().intValue());
        assertThat(instancesInfo.getNumInstances().intValue()).isEqualTo(
                report.getNumInstances().intValue());
        assertThat(instancesInfo.getNumAttributes().intValue()).isEqualTo(
                report.getNumAttributes().intValue());
    }

    @Test
    void testMapInstancesInfo() {
        InstancesInfo instancesInfo = buildInstancesInfo();
        InstancesReport instancesReport = instancesMapper.map(instancesInfo);
        assertThat(instancesReport.getRelationName()).isEqualTo(instancesInfo.getRelationName());
        assertThat(instancesReport.getNumClasses().intValue()).isEqualTo(
                instancesInfo.getNumClasses().intValue());
        assertThat(instancesReport.getNumInstances().intValue()).isEqualTo(
                instancesInfo.getNumInstances().intValue());
        assertThat(instancesReport.getNumAttributes().intValue()).isEqualTo(
                instancesInfo.getNumAttributes().intValue());
        assertThat(instancesReport.getDataMd5Hash()).isEqualTo(instancesInfo.getDataMd5Hash());
    }
}
