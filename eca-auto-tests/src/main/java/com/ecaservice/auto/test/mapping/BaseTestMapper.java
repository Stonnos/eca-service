package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.BaseTestDto;
import com.ecaservice.auto.test.entity.autotest.BaseTestEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Base mapper.
 *
 * @param <S> - source generic type
 * @param <T> - target generic type
 * @author Roman Batygin
 */
public abstract class BaseTestMapper<S extends BaseTestEntity, T extends BaseTestDto> {

    private final Class<S> type;

    /**
     * Constructor with parameters.
     *
     * @param type - object type
     */
    protected BaseTestMapper(Class<S> type) {
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
     * Maps object.
     *
     * @param object - source object
     * @return target object
     */
    public abstract T map(S object);

    /**
     * Maps total time.
     *
     * @param baseTestEntity - base test entity
     * @param baseTestDto    - base test dto
     */
    @AfterMapping
    void mapTotalTime(BaseTestEntity baseTestEntity,
                      @MappingTarget BaseTestDto baseTestDto) {
        baseTestDto.setTotalTime(totalTime(baseTestEntity.getStarted(), baseTestDto.getFinished()));
    }
}
