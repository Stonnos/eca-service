package com.ecaservice.test.common.service;

import java.util.List;

/**
 * Test data provider interface.
 *
 * @param <T> - config generic type
 * @author Roman Batygin
 */
public interface TestDataProvider<T> {

    /**
     * Gets total test data count.
     *
     * @return test data count
     */
    int count();

    /**
     * Gets test data by specified index.
     *
     * @param index - data index
     * @return test data object
     */
    T getTestData(int index);

    /**
     * Gets test data list.
     *
     * @return test data list
     */
    List<T> getTestDataModels();
}
