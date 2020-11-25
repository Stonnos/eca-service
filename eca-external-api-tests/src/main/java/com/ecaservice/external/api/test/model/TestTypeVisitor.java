package com.ecaservice.external.api.test.model;

/**
 * Datasource type visitor interface pattern.
 *
 * @author Roman Batygin
 */
public interface TestTypeVisitor {

    /**
     * Visit in case id datasource type is USING_EXTERNAL_DATA_URL.
     */
    void testUsingExternalDataUrl() throws Exception;

    /**
     * Visit in case id datasource type is USING_API_DATA_STORAGE.
     */
    void testUsingApiDataStorage() throws Exception;
}
