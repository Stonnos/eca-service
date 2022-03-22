package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.AutoTestsJobDto;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import org.mapstruct.Mapper;

/**
 * Auto tests mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface AutoTestsMapper {

    /**
     * Maps auto tests job entity to dto model.
     *
     * @param autoTestsJobEntity - auto tests job entity
     * @return auto tests job dto
     */
    AutoTestsJobDto map(AutoTestsJobEntity autoTestsJobEntity);
}
