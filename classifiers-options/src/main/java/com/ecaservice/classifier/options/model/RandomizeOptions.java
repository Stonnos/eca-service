package com.ecaservice.classifier.options.model;

/**
 * Randomize options.
 *
 * @author Roman Batygin
 */
public interface RandomizeOptions {

    /**
     * Gets seed value.
     *
     * @return seed value
     */
    Integer getSeed();

    /**
     * Sets seed value.
     *
     * @param seed - seed value
     */
    void setSeed(Integer seed);
}
