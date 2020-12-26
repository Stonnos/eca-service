package com.ecaservice.external.api.test.exception;

/**
 * Exception occurs when train data has been uploaded with error.
 *
 * @author Roman Batygin
 */
public class UploadTrainDataException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param testId - auto test id
     */
    public UploadTrainDataException(Long testId) {
        super(String.format("Train data has been uploaded with error for test [%d]", testId));
    }
}
