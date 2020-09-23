package com.ecaservice.oauth.util;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
     * Gets local date time in milliseconds.
     *
     * @param localDateTime - local date time
     * @return ocal date time in milliseconds
     */
    public static long toMillis(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Generates unique token by algorithm:
     * 1. Creates salt in format uuid:user_creation_date_millis
     * 2. Gets md5_salt = MD5(salt)
     * 3. Creates string in format md5_salt:token_creation_date_millis
     * 4. Gets results = base64(md5_salt:token_creation_date_millis)
     *
     * @param userEntity - user entity
     * @return experiment token
     */
    public static String generateToken(UserEntity userEntity) {
        LocalDateTime tokenCreationDate = LocalDateTime.now();
        String uuid = UUID.randomUUID().toString();
        String salt = String.format(SALT_FORMAT, uuid, toMillis(userEntity.getCreationDate()));
        String md5Salt = DigestUtils.md5DigestAsHex(salt.getBytes(StandardCharsets.UTF_8));
        String stringToEncode = String.format(SALT_FORMAT, md5Salt, toMillis(tokenCreationDate));
        return Base64Utils.encodeToString(stringToEncode.getBytes(StandardCharsets.UTF_8));
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
}
