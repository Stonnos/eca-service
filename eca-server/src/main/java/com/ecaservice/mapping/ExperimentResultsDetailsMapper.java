package com.ecaservice.mapping;

import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import org.mapstruct.Mapper;

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
}
