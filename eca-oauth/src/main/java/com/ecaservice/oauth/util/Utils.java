package com.ecaservice.oauth.util;

import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.user.model.Role;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String ATTACHMENT = "attachment";
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

    /**
     * Creates response with attachment.
     *
     * @param data     - data as bytes array
     * @param fileName - attachment file name
     * @return response entity
     */
    public static ResponseEntity<ByteArrayResource> buildAttachmentResponse(byte[] data, String fileName) {
        ByteArrayResource resource = new ByteArrayResource(data);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, fileName);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    /**
     * Checks if user has role super admin.
     *
     * @param userEntity - user entity
     * @return {@code true} if user is super admin
     */
    public static boolean isSuperAdmin(UserEntity userEntity) {
        return userEntity.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .anyMatch(Role.ROLE_SUPER_ADMIN::equals);
    }
}
