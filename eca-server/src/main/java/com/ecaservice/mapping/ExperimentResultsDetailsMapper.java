package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

/**
 * Implements experiment results to experiment details mapping.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {StatisticsReportMapper.class, ClassificationCostsMapper.class, ClassifierInfoMapper.class})
public abstract class ExperimentResultsDetailsMapper {

    /**
     * Maps experiment results entity and experiment results details.
     *
     * @param experimentResultsEntity - experiment results details mapper
     * @return experiment results details dto
     */
    public abstract ExperimentResultsDetailsDto map(ExperimentResultsEntity experimentResultsEntity);

    /**
     * Updates experiment results details.
     *
     * @param evaluationResultsSimpleResponse - evaluation results simple response
     * @param experimentResultsDetailsDto     - experiment results details dto
     */
    @Mappings({
            @Mapping(source = "statistics", target = "evaluationResultsDto")
    })
    public abstract void update(GetEvaluationResultsResponse evaluationResultsSimpleResponse,
                                @MappingTarget ExperimentResultsDetailsDto experimentResultsDetailsDto);
}
