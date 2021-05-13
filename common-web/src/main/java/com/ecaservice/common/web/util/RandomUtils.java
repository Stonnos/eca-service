package com.ecaservice.common.web.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Random utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class RandomUtils {

    private static final String SALT_FORMAT = "%s:%d";

    /**
     * Generates unique token by algorithm:
     * 1. Creates salt in format uuid:first_random_number
     * 2. Gets md5_salt = MD5(salt)
     * 3. Creates string in format md5_salt:second_random_number
     * 4. Gets results = base64(md5_salt:second_random_number)
     *
     * @return token value
     */
    public static String generateToken() {
        long first = secureRandomNumber();
        long second = secureRandomNumber();
        String uuid = UUID.randomUUID().toString();
        String salt = String.format(SALT_FORMAT, uuid, first);
        String md5Salt = DigestUtils.md5DigestAsHex(salt.getBytes(StandardCharsets.UTF_8));
        String stringToEncode = String.format(SALT_FORMAT, md5Salt, second);
        return Base64Utils.encodeToString(stringToEncode.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates secure random number.
     *
     * @return random number
     */
    public static long secureRandomNumber() {
        SecureRandom secureRandom = new SecureRandom();
        return Math.abs(secureRandom.nextLong());
    }
}
