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
    USING_EXTERNAL_DATA_URL {
        @Override
        public void apply(TestTypeVisitor visitor) throws Exception {
            visitor.testUsingExternalDataUrl();
        }
    },

    /**
     * Test case in which train data is uploaded to internal data storage API
     */
    USING_API_DATA_STORAGE {
        @Override
        public void apply(TestTypeVisitor visitor) throws Exception {
            visitor.testUsingApiDataStorage();
        }
    };

    /**
     * Visitor method.
     *
     * @param visitor - visitor interface
     */
    public abstract void apply(TestTypeVisitor visitor) throws Exception;
}
