package com.ecaservice.service.experiment;


import weka.core.Instances;

import java.io.File;

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
}
