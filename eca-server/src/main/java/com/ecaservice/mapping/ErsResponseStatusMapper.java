package com.ecaservice.mapping;

import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.model.entity.ErsResponseStatus;
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
