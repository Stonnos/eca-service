package com.ecaservice.classifier.template.processor.util;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.IterativeEnsembleOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.io.IOException;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String LINE_BREAK = "\n";
    private static final String COLON = ": ";

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

    /**
     * Gets classifier options details string.
     *
     * @param classifierInfoDto - classifier info dto
     * @return classifier options details string
     */
    public static String getClassifierOptionsDetailsString(ClassifierInfoDto classifierInfoDto) {
        StringBuilder classifierOptions = new StringBuilder();
        classifierInfoDto.getInputOptions().forEach(inputOptionDto -> {
            if (!CollectionUtils.isEmpty(inputOptionDto.getIndividualClassifiers())) {
                classifierOptions.append(LINE_BREAK)
                        .append(inputOptionDto.getOptionName())
                        .append(COLON);
                inputOptionDto.getIndividualClassifiers().forEach(individualClassifierInfo -> {
                    classifierOptions.append(LINE_BREAK)
                            .append(individualClassifierInfo.getClassifierDescription())
                            .append(LINE_BREAK);
                    individualClassifierInfo.getInputOptions().forEach(individualClassifierOptions -> {
                        classifierOptions.append(individualClassifierOptions.getOptionName())
                                .append(COLON)
                                .append(individualClassifierOptions.getOptionValue())
                                .append(LINE_BREAK);
                    });
                });
            } else {
                classifierOptions.append(inputOptionDto.getOptionName())
                        .append(COLON)
                        .append(inputOptionDto.getOptionValue())
                        .append(LINE_BREAK);
            }
        });
        return classifierOptions.toString();
    }
}
