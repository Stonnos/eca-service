package com.ecaservice.external.api.test.mapping;

import com.ecaservice.external.api.test.dto.AbstractAutoTestDto;
import com.ecaservice.external.api.test.dto.BaseTestDto;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.BaseEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Base auto test request mapper.
 *
 * @param <S> - source entity generic type
 * @param <T> - target dto generic type
 * @author Roman Batygin
 */
public abstract class BaseAutoTestRequestMapper<S extends AutoTestEntity, T extends AbstractAutoTestDto> {

    private final Class<S> type;

    /**
     * Constructor with parameters.
     *
     * @param type - object type
     */
    protected BaseAutoTestRequestMapper(Class<S> type) {
        this.type = type;
    }

    /**
     * Can map object.
     *
     * @param object - object
     * @return {@code true} if object can map, otherwise {@code false}
     */
    public boolean canMap(S object) {
        return object != null && type.isAssignableFrom(object.getClass());
    }

    /**
     * Maps auto tests entity to dto model.
     *
     * @param autoTestEntity - auto tests entity
     * @return auto tests dto
     */
    public abstract T map(S autoTestEntity);

    /**
     * Maps total time.
     *
     * @param baseTestEntity - base test entity
     * @param baseTestDto    - base test dto
     */
    @AfterMapping
    void mapTotalTime(BaseEntity baseTestEntity, @MappingTarget BaseTestDto baseTestDto) {
        baseTestDto.setTotalTime(totalTime(baseTestEntity.getStarted(), baseTestDto.getFinished()));
    }
}
