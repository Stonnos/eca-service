package com.ecaservice.load.test.service;

/**
 * Config service interface.
 *
 * @param <T> - config generic type
 * @author Roman Batygin
 */
public interface ConfigService<T> {

    /**
     * Gets total configs size.
     *
     * @return configs size
     */
    int size();

    /**
     * Gets config by specified index.
     *
     * @param index - config index
     * @return config object
     */
    T getConfig(int index);
}
