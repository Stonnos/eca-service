package com.ecaservice.external.api.test.model;

/**
 * Datasource type visitor interface pattern.
 *
 * @author Roman Batygin
 */
public interface DataSourceTypeVisitor {

    /**
     * Visit in case id datasource type is EXTERNAL.
     */
    void visitExternal() throws Exception;

    /**
     * Visit in case id datasource type is INTERNAL.
     */
    void visitInternal() throws Exception;
}
