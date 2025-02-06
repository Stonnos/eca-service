package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.ExperimentResultsDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Experiment results mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = ExperimentMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ExperimentResultsMapper {

    /**
     * Maps experiment results entity to its dto model.
     *
     * @param experimentResultsEntity - experiment results entity
     * @return experiment results dto
     */
    @Mapping(target = "classifierInfo", ignore = true)
    ExperimentResultsDto map(ExperimentResultsEntity experimentResultsEntity);

    /**
     * Maps experiment results entity and experiment results details.
     *
     * @param experimentResultsEntity - experiment results details mapper
     * @return experiment results details dto
     */
    @Mapping(source = "experiment", target = "experimentDto")
    @Mapping(target = "classifierInfo", ignore = true)
    ExperimentResultsDetailsDto mapDetails(ExperimentResultsEntity experimentResultsEntity);
}
