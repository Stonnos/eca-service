package com.ecaservice.server.service;

import com.ecaservice.web.dto.model.ClassifyInstanceRequestDto;
import com.ecaservice.web.dto.model.ClassifyInstanceResultDto;

/**
 * Classify instance service.
 *
 * @author Roman Batygin
 */
public interface ClassifyInstanceService {

    /**
     * Classify specified instance.
     *
     * @param classifyInstanceRequestDto - classify instance request dto
     * @return classify instance result
     */
    ClassifyInstanceResultDto classifyInstance(ClassifyInstanceRequestDto classifyInstanceRequestDto);
}
