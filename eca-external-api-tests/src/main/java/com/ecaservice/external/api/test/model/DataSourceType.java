package com.ecaservice.external.api.test.model;

/**
 * Train data source type.
 *
 * @author Roman Batygin
 */
public enum DataSourceType {

    /**
     * External data source (from http or ftp server)
     */
    EXTERNAL {
        @Override
        public void apply(DataSourceTypeVisitor visitor) throws Exception {
            visitor.visitExternal();
        }
    },

    /**
     * Data uploaded to the api server will be used
     */
    INTERNAL {
        @Override
        public void apply(DataSourceTypeVisitor visitor) throws Exception {
            visitor.visitInternal();
        }
    };

    /**
     * Visitor method.
     *
     * @param visitor - visitor interface
     */
    public abstract void apply(DataSourceTypeVisitor visitor) throws Exception;
}
