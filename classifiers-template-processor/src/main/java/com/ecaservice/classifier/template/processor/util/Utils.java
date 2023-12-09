package com.ecaservice.classifier.template.processor.util;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.IterativeEnsembleOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Checks if classifier options is ensemble classifier options.
     *
     * @param classifierOptions - classifier options
     * @return {@code true} if classifier options is ensemble classifier options
     */
    public static boolean isEnsembleClassifierOptions(ClassifierOptions classifierOptions) {
        return classifierOptions instanceof IterativeEnsembleOptions || classifierOptions instanceof StackingOptions;
    }

    /**
     * Converts classifier options to json string.
     *
     * @param classifierOptions - classifier options
     * @return classifier options json string
     */
    public static String toJsonString(ClassifierOptions classifierOptions) {
        try {
            return objectMapper.writeValueAsString(classifierOptions);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    String.format("There was an error while parsing object [%s]: %s", classifierOptions,
                            ex.getMessage()));
        }
    }

    /**
     * Parses classifier options json string.
     *
     * @param options - classifier options json string
     * @return classifier options object
     */
    public static ClassifierOptions parseOptions(String options) {
        try {
            return objectMapper.readValue(options, ClassifierOptions.class);
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
}
