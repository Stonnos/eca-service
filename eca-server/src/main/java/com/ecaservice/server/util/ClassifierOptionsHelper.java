package com.ecaservice.server.util;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.IterativeEnsembleOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import com.ecaservice.server.exception.ClassifierOptionsException;
import com.ecaservice.server.exception.InvalidClassifierOptionsFormatException;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Classifier options helper.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class ClassifierOptionsHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates classifier options database model.
     *
     * @param classifierOptions        - classifier options
     * @param classifiersConfiguration - classifiers configuration entity
     * @return classifier options database model entity
     */
    public static ClassifierOptionsDatabaseModel createClassifierOptionsDatabaseModel(
            ClassifierOptions classifierOptions, ClassifiersConfiguration classifiersConfiguration) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setOptionsName(classifierOptions.getClass().getSimpleName());
        String config = toJsonString(classifierOptions);
        classifierOptionsDatabaseModel.setConfigMd5Hash(
                DigestUtils.md5DigestAsHex(config.getBytes(StandardCharsets.UTF_8)));
        classifierOptionsDatabaseModel.setConfig(config);
        classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
        classifierOptionsDatabaseModel.setConfiguration(classifiersConfiguration);
        return classifierOptionsDatabaseModel;
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
            throw new ClassifierOptionsException(
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
            throw new ClassifierOptionsException(ex.getMessage());
        }
    }

    /**
     * Parses classifier options input stream.
     *
     * @param inputStream - classifier options as input stream
     * @return classifier options object
     */
    public static ClassifierOptions parseOptions(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, ClassifierOptions.class);
        } catch (IOException ex) {
            log.error("Error while parse classifier options from input stream: {}", ex.getMessage());
            throw new InvalidClassifierOptionsFormatException();
        }
    }

    /**
     * Checks if classifier options is ensemble classifier options.
     *
     * @param classifierOptions - classifier options
     * @return {@code true} if classifier options is ensemble classifier options
     */
    public static boolean isEnsembleClassifierOptions(ClassifierOptions classifierOptions) {
        return classifierOptions instanceof IterativeEnsembleOptions || classifierOptions instanceof StackingOptions;
    }
}
