package com.ecaservice.external.api.test.model;

/**
 * Test type.
 *
 * @author Roman Batygin
 */
public enum TestType {

    /**
     * Test case in which train data is loaded from external data source (from http or ftp server)
     */
    USING_EXTERNAL_DATA_URL,

    /**
     * Test case in which train data is uploaded to internal data storage API
     */
    USING_API_DATA_STORAGE
}
