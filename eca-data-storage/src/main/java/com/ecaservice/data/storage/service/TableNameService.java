package com.ecaservice.data.storage.service;

/**
 * Table name service.
 *
 * @author Roman Batygin
 */
public interface TableNameService {

    /**
     * Checks table existing in database.
     *
     * @param tableName - table name
     * @return {@code true} if table name is exists in database
     */
    boolean tableExists(String tableName);
}
