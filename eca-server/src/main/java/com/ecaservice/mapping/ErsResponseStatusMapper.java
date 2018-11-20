package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.web.dto.ErsResponseStatus;
import org.mapstruct.Mapper;

/**
 * ERS response status mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ErsResponseStatusMapper {

    /**
     * Maps ERS response status to its dto model.
     *
     * @param responseStatus - ERS response status
     * @return ERS response status dto enum
     */
    ErsResponseStatus map(ResponseStatus responseStatus);
}
