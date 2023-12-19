package com.ecaservice.common.web.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Random utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class RandomUtils {

    private static final String TOKEN_FORMAT = "%s:%d";

    /**
     * Generates unique token by algorithm:
     * 1. Creates string in format uuid:current_timestamp_millis
     * 2. Calculate token = base64(uuid:current_timestamp_millis)
     *
     * @return token value
     */
    public static String generateToken() {
        long currentTimestampMillis = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        String stringToEncode = String.format(TOKEN_FORMAT, uuid, currentTimestampMillis);
        return Base64Utils.encodeToString(stringToEncode.getBytes(StandardCharsets.UTF_8));
    }
}
