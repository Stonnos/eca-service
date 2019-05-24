package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.RocCurvePoint;
import com.ecaservice.web.dto.model.RocCurvePointDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Roc - curve point mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface RocCurvePointMapper {

    /**
     * Maps ERS roc - curve point to its dto model.
     *
     * @param rocCurvePoint - roc - curve point
     * @return roc - curve point dto
     */
    RocCurvePointDto map(RocCurvePoint rocCurvePoint);

    /**
     * Maps ERS roc - curve points list to its dto models.
     *
     * @param rocCurvePoints - roc - curve points list
     * @return roc - curve point dto list
     */
    List<RocCurvePointDto> map(List<RocCurvePoint> rocCurvePoints);
}
