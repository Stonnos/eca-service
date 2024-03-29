package com.ecaservice.server.model.entity;

import com.ecaservice.server.model.dictionary.ErsResponseStatusDictionary;
import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * ERS response status enum.
 *
 * @author Roman batygin
 */
@RequiredArgsConstructor
public enum ErsResponseStatus implements DescriptiveEnum {

    SUCCESS(ErsResponseStatusDictionary.SUCCESS_DESCRIPTION),
    DUPLICATE_REQUEST_ID(ErsResponseStatusDictionary.DUPLICATE_REQUEST_ID_DESCRIPTION),
    ERROR(ErsResponseStatusDictionary.ERROR_DESCRIPTION),
    DATA_NOT_FOUND(ErsResponseStatusDictionary.DATA_NOT_FOUND_DESCRIPTION),
    RESULTS_NOT_FOUND(ErsResponseStatusDictionary.RESULTS_NOT_FOUND_DESCRIPTION),
    SERVICE_UNAVAILABLE(ErsResponseStatusDictionary.SERVICE_UNAVAILABLE_DESCRIPTION);

    private final String description;

    /**
     * ERS response status description.
     *
     * @return ERS response status status description
     */
    @Override
    public String getDescription() {
        return description;
    }
}
