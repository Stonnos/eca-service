package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.RocCurveData;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Roc - curve data mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = RocCurvePointMapper.class)
public interface RocCurveDataMapper {

    /**
     * Maps ERS roc - curve data to its dto model.
     *
     * @param rocCurveData - roc - curve data
     * @return roc - curve data dto
     */
    RocCurveDataDto map(RocCurveData rocCurveData);

    /**
     * Maps ERS roc - curve data list to its dto models.
     *
     * @param rocCurveDataList - roc - curve data list
     * @return roc - curve data dto list
     */
    List<RocCurveDataDto> map(List<RocCurveData> rocCurveDataList);
}
