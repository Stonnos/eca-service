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

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String ATTACHMENT = "attachment";

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
