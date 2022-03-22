package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.BaseEvaluationRequestDto;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Evaluation request dto mapper.
 *
 * @param <S> - source evaluation request entity
 * @param <T> - target evaluation request dto
 * @author Roman Batygin
 */
public abstract class BaseEvaluationRequestMapper<S extends BaseEvaluationRequestEntity, T extends BaseEvaluationRequestDto> {

    private final Class<S> type;

    /**
     * Constructor with parameters.
     *
     * @param type - evaluation request type
     */
    protected BaseEvaluationRequestMapper(Class<S> type) {
        this.type = type;
    }

    /**
     * Can map evaluation request entity.
     *
     * @param request - evaluation request entity
     * @return {@code true} if evaluation request entity can map, otherwise {@code false}
     */
    public boolean canMap(S request) {
        return request != null && type.isAssignableFrom(request.getClass());
    }

    /**
     * Maps evaluation request entity to dto model.
     *
     * @param request - evaluation request entity
     * @return evaluation request dto
     */
    public abstract T map(S request);

    /**
     * Maps total time.
     *
     * @param baseEvaluationRequestEntity - base evaluation request entity
     * @param baseEvaluationRequestDto    - base evaluation request dto
     */
    @AfterMapping
    void mapTotalTime(BaseEvaluationRequestEntity baseEvaluationRequestEntity,
                      @MappingTarget BaseEvaluationRequestDto baseEvaluationRequestDto) {
        baseEvaluationRequestDto.setTotalTime(totalTime(baseEvaluationRequestEntity.getStarted(),
                baseEvaluationRequestDto.getFinished()));
    }
}
