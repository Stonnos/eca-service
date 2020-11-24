package com.ecaservice.external.api.test.model;

/**
 * Datasource type visitor interface pattern.
 *
 * @author Roman Batygin
 */
public interface TestTypeVisitor {

    /**
     * Visit in case id datasource type is EXTERNAL.
     */
    void testUsingExternalDataUrl() throws Exception;

    /**
     * Visit in case id datasource type is INTERNAL.
     */
    void testUsingApiDataStorage() throws Exception;
}
