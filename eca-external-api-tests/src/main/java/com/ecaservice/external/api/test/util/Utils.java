package com.ecaservice.external.api.test.util;

import com.ecaservice.external.api.dto.ResponseDto;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

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

    public static <P, V> V getValue(ResponseDto<P> responseDto, Function<P, V> valueFunction) {
        return Optional.ofNullable(responseDto).map(ResponseDto::getPayload).map(valueFunction).orElse(null);
    }
}
