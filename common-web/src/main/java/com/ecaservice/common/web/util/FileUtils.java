package com.ecaservice.common.web.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * File utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FileUtils {

    /**
     * Checks file name has valid extension.
     *
     * @param fileName            - file name
     * @param extensionsWhitelist - valid extensions list
     * @return {@code true} if file name has valid extension
     */
    public static boolean isValidExtension(String fileName, List<String> extensionsWhitelist) {
        if (StringUtils.isNotEmpty(fileName) && !CollectionUtils.isEmpty(extensionsWhitelist)) {
            String extension = FilenameUtils.getExtension(fileName);
            return extensionsWhitelist.contains(extension);
        } else {
            return false;
        }
    }
}
