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
    EXTERNAL,

    /**
     * Data uploaded to the api server will be used
     */
    INTERNAL
}
