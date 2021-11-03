package com.ecaservice.server.mapping;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

/**
 * ERS response status mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ErsResponseStatusMapper {

    /**
     * Maps ERS error code to its internal model.
     *
     * @param ersErrorCode - ERS error code
     * @return ERS response status internal enum
     */
    ErsResponseStatus map(ErsErrorCode ersErrorCode);

    /**
     * Maps ers error code to internal error code.
     *
     * @param ersErrorCode - ers error code
     * @return error code
     */
    @ValueMapping(source = "DUPLICATE_REQUEST_ID", target = "INTERNAL_SERVER_ERROR")
    @ValueMapping(source = "DATA_NOT_FOUND", target = "TRAINING_DATA_NOT_FOUND")
    @ValueMapping(source = "RESULTS_NOT_FOUND", target = "CLASSIFIER_OPTIONS_NOT_FOUND")
    ErrorCode mapErrorCode(ErsErrorCode ersErrorCode);
}
