package com.ecaservice.util;

import com.ecaservice.exception.ClassifierOptionsException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.options.ClassifierOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates classifier options database model.
     *
     * @param classifierOptions        - classifier options
     * @param classifiersConfiguration - classifiers configuration entity
     * @return classifier options database model entity
     */
    public static ClassifierOptionsDatabaseModel createClassifierOptionsDatabaseModel(
            ClassifierOptions classifierOptions, ClassifiersConfiguration classifiersConfiguration) {
        try {
            ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
            classifierOptionsDatabaseModel.setOptionsName(classifierOptions.getClass().getSimpleName());
            String config = objectMapper.writeValueAsString(classifierOptions);
            classifierOptionsDatabaseModel.setConfigMd5Hash(
                    DigestUtils.md5DigestAsHex(config.getBytes(StandardCharsets.UTF_8)));
            classifierOptionsDatabaseModel.setConfig(config);
            classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
            classifierOptionsDatabaseModel.setConfiguration(classifiersConfiguration);
            return classifierOptionsDatabaseModel;
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
        } catch (Exception ex) {
            throw new ClassifierOptionsException(ex.getMessage());
        }
    }

    /**
     * Checks classifier options json string deserialization.
     *
     * @param options - classifier options json string
     * @return {@code true} if classifier options json string can be deserialize
     */
    public static boolean isParsableOptions(String options) {
        if (StringUtils.isEmpty(options)) {
            return false;
        } else {
            try {
                parseOptions(options);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
}
