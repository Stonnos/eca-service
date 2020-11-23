package com.ecaservice.external.api.test.util;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Copy resource into byte array.
     *
     * @param resource - resource object
     * @return byte array
     * @throws IOException in case of I/O error
     */
    public static byte[] copyToByteArray(Resource resource) throws IOException {
        @Cleanup InputStream inputStream = resource.getInputStream();
        @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, outputStream);
        return outputStream.toByteArray();
    }
}
