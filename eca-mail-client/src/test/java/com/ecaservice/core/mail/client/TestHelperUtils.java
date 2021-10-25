package com.ecaservice.core.mail.client;

import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.core.mail.client.entity.EmailRequestStatus;
import com.ecaservice.notification.dto.EmailRequest;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Map;

import static com.ecaservice.core.mail.client.util.Utils.toJson;
import static com.ecaservice.notification.util.Priority.LOW;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String TEMPLATE_CODE = "TemplateCode";
    private static final String RECEIVER = "receiver@mail.ru";
    private static final Map<String, String>
            VARIABLES = Map.of("Var1", "1", "Var2", "23");

    /**
     * Creates email request.
     *
     * @return email request
     */
    public static EmailRequest createEmailRequest() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTemplateCode(TEMPLATE_CODE);
        emailRequest.setReceiver(RECEIVER);
        emailRequest.setVariables(VARIABLES);
        emailRequest.setPriority(LOW);
        return emailRequest;
    }

    /**
     * Creates email request entity.
     *
     * @param requestStatus - request status
     * @param expiredAt     - expired at date
     * @return email request entity
     */
    public static EmailRequestEntity createEmailRequestEntity(EmailRequestStatus requestStatus,
                                                              LocalDateTime expiredAt) {
        EmailRequestEntity emailRequestEntity = new EmailRequestEntity();
        emailRequestEntity.setTemplateCode(TEMPLATE_CODE);
        emailRequestEntity.setReceiver(RECEIVER);
        emailRequestEntity.setPriority(LOW);
        emailRequestEntity.setCreated(LocalDateTime.now());
        emailRequestEntity.setVariablesJson(toJson(VARIABLES));
        emailRequestEntity.setRequestStatus(requestStatus);
        emailRequestEntity.setExpiredAt(expiredAt);
        return emailRequestEntity;
    }
}
