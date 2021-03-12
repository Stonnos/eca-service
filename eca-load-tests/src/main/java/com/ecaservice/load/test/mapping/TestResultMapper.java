package com.ecaservice.load.test.mapping;

import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.load.test.entity.TestResult;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

/**
 * Test result mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface TestResultMapper {

    /**
     * Maps technical status to test result.
     *
     * @param technicalStatus - technical status
     * @return test result
     */
    @ValueMapping(source = "SUCCESS", target = "PASSED")
    @ValueMapping(source = "ERROR", target = "FAILED")
    @ValueMapping(source = "VALIDATION_ERROR", target = "FAILED")
    @ValueMapping(source = "TIMEOUT", target = "FAILED")
    TestResult map(TechnicalStatus technicalStatus);
}
