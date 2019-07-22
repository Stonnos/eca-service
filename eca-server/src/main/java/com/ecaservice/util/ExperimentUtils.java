package com.ecaservice.util;

import com.ecaservice.model.entity.Experiment;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

/**
 * Experiment utility class.
 *
 * @author Roman Batygin
 */
public class ExperimentUtils {

    private ExperimentUtils() {
    }

    /**
     * Gets experiment file.
     *
     * @param experiment       - experiment entity
     * @param filePathFunction - file path predicate
     * @return file object
     */
    public static File getExperimentFile(Experiment experiment, Function<Experiment, String> filePathFunction) {
        return Optional.ofNullable(experiment).map(filePathFunction).map(File::new).orElse(null);
    }
}
