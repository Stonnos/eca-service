package com.ecaservice.server.service;

import com.ecaservice.web.dto.model.RocCurveDataDto;

/**
 * Roc curve data provider interface.
 *
 * @author Roman Batygin
 */
public interface RocCurveDataProvider {

    /**
     * Gets roc curve data.
     *
     * @param modelId         - model id
     * @param classValueIndex - class value index
     * @return roc curve data dto
     */
    RocCurveDataDto getRocCurveData(Long modelId, Integer classValueIndex);
}
