package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import org.mapstruct.Mapper;

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
}
