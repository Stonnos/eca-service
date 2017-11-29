package com.ecaservice.service.experiment;


import eca.converters.model.ExperimentHistory;
import weka.core.Instances;

import java.io.File;

/**
 * Data service interface.
 *
 * @author Roman Batygin
 */
public interface DataService {

    /**
     * Saves data to file.
     *
     * @param file file object
     * @param data {@link Instances} object
     * @throws Exception
     */
    void save(File file, Instances data) throws Exception;

    /**
     * Loads data from file.
     *
     * @param file file object
     * @return {@link Instances} object
     * @throws Exception
     */
    Instances load(File file) throws Exception;

    /**
     * Saves experiment history to file.
     *
     * @param file              file object
     * @param experimentHistory {@link ExperimentHistory} object
     * @throws Exception
     */
    void save(File file, ExperimentHistory experimentHistory) throws Exception;

    /**
     * Deletes file from disk.
     *
     * @param file file object
     */
    void delete(File file);
}
