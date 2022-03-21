package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.AutoTestsJobDto;
import com.ecaservice.auto.test.dto.BaseEvaluationRequestDto;
import com.ecaservice.auto.test.dto.EvaluationRequestDto;
import com.ecaservice.auto.test.dto.ExperimentRequestDto;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static com.ecaservice.test.common.util.Utils.totalTime;

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

    /**
     * Maps experiment request entity to dto model.
     *
     * @param experimentRequestEntity - experiment request entity
     * @return experiment request dto
     */
    @Mapping(source = "evaluationMethod.description", target = "evaluationMethodDescription")
    @Mapping(source = "experimentType.description", target = "experimentTypeDescription")
    ExperimentRequestDto map(ExperimentRequestEntity experimentRequestEntity);

    /**
     * Maps evaluation request entity to dto model.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return evaluation request dto
     */
    @Mapping(source = "evaluationMethod.description", target = "evaluationMethodDescription")
    EvaluationRequestDto map(EvaluationRequestEntity evaluationRequestEntity);

    /**
     * Maps total time.
     *
     * @param baseEvaluationRequestEntity - base evaluation request entity
     * @param baseEvaluationRequestDto    - base evaluation request dto
     */
    @AfterMapping
    default void mapTotalTime(BaseEvaluationRequestEntity baseEvaluationRequestEntity,
                              @MappingTarget BaseEvaluationRequestDto baseEvaluationRequestDto) {
        baseEvaluationRequestDto.setTotalTime(totalTime(baseEvaluationRequestEntity.getStarted(),
                baseEvaluationRequestDto.getFinished()));
    }
}
