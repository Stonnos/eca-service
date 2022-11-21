package com.ecaservice.external.api.test.model;

/**
 * Train data source.
 *
 * @author Roman Batygin
 */
public enum TrainDataSource {

    /**
     * Train data is loaded from external data source (from http or ftp server)
     */
    EXTERNAL_DATA_URL,

    /**
     * Train data is uploaded to internal data storage API
     */
    API_DATA_STORAGE
}
