package com.ecaservice.external.api.test.mapping;

import com.ecaservice.external.api.test.dto.AutoTestDto;
import com.ecaservice.external.api.test.dto.AutoTestsJobDto;
import com.ecaservice.external.api.test.dto.BaseTestDto;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.BaseEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Auto test mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface AutoTestMapper {

    /**
     * Maps auto tests job entity to dto model.
     *
     * @param jobEntity - auto tests job entity
     * @return auto tests job dto
     */
    AutoTestsJobDto map(JobEntity jobEntity);

    /**
     * Maps auto tests entity to dto model.
     *
     * @param autoTestEntity - auto tests entity
     * @return auto tests dto
     */
    AutoTestDto map(AutoTestEntity autoTestEntity);

    /**
     * Maps total time.
     *
     * @param baseTestEntity - base test entity
     * @param baseTestDto    - base test dto
     */
    @AfterMapping
    default void mapTotalTime(BaseEntity baseTestEntity, @MappingTarget BaseTestDto baseTestDto) {
        baseTestDto.setTotalTime(totalTime(baseTestEntity.getStarted(), baseTestDto.getFinished()));
    }
}
