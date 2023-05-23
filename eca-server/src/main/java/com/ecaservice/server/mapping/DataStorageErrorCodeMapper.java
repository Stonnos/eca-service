package com.ecaservice.server.mapping;

import com.ecaservice.data.storage.dto.DsErrorCode;
import com.ecaservice.server.error.EsErrorCode;
import com.ecaservice.server.service.ds.DsInternalErrorCode;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

/**
 * Data storage error code mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface DataStorageErrorCodeMapper {

    /**
     * Maps data storage error code to internal error code.
     *
     * @param dsErrorCode - data storage error code
     * @return internal error code
     */
    @ValueMapping(source = "CLASS_ATTRIBUTE_NOT_SELECTED", target = "CLASS_ATTRIBUTE_NOT_SELECTED")
    @ValueMapping(source = "SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW", target = "SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW")
    @ValueMapping(source = "CLASS_VALUES_IS_TOO_LOW", target = "CLASS_VALUES_IS_TOO_LOW")
    @ValueMapping(source = "INSTANCES_NOT_FOUND", target = "INSTANCES_NOT_FOUND")
    DsInternalErrorCode mapErrorCode(DsErrorCode dsErrorCode);

    /**
     * Maps data storage internal error code to service error code.
     *
     * @param dsErrorCode - data storage error code
     * @return error code
     */
    @ValueMapping(source = "CLASS_ATTRIBUTE_NOT_SELECTED", target = "CLASS_ATTRIBUTE_NOT_SELECTED")
    @ValueMapping(source = "SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW", target = "SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW")
    @ValueMapping(source = "CLASS_VALUES_IS_TOO_LOW", target = "CLASS_VALUES_IS_TOO_LOW")
    @ValueMapping(source = "INSTANCES_NOT_FOUND", target = "INSTANCES_NOT_FOUND")
    EsErrorCode mapErrorCode(DsInternalErrorCode dsErrorCode);
}
