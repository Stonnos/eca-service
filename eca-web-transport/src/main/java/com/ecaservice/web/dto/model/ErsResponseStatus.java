package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.dictionary.ErsResponseStatusDictionary;

/**
 * ERS response status enum.
 *
 * @author Roman batygin
 */
public enum ErsResponseStatus {

    SUCCESS(ErsResponseStatusDictionary.SUCCESS_DESCRIPTION),
    INVALID_REQUEST_ID(ErsResponseStatusDictionary.INVALID_REQUEST_ID_DESCRIPTION),
    DUPLICATE_REQUEST_ID(ErsResponseStatusDictionary.DUPLICATE_REQUEST_ID_DESCRIPTION),
    ERROR(ErsResponseStatusDictionary.ERROR_DESCRIPTION),
    INVALID_REQUEST_PARAMS(ErsResponseStatusDictionary.INVALID_REQUEST_PARAMS_DESCRIPTION),
    DATA_NOT_FOUND(ErsResponseStatusDictionary.DATA_NOT_FOUND_DESCRIPTION),
    RESULTS_NOT_FOUND(ErsResponseStatusDictionary.RESULTS_NOT_FOUND);

    private String description;

    ErsResponseStatus(String description) {
        this.description = description;
    }

    /**
     * ERS response status description.
     *
     * @return ERS response status status description
     */
    public String getDescription() {
        return description;
    }
}
