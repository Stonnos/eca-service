package com.notification.util;

import com.notification.dto.EmailResponse;
import lombok.experimental.UtilityClass;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Creates email response.
     *
     * @param requestId - request id
     * @return email response
     */
    public static EmailResponse buildResponse(String requestId) {
        EmailResponse emailResponse = new EmailResponse();
        emailResponse.setRequestId(requestId);
        return emailResponse;
    }
}
