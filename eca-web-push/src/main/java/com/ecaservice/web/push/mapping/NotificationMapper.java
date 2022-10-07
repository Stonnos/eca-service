package com.ecaservice.web.push.mapping;

import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.NotificationEntity;
import com.ecaservice.web.push.entity.NotificationParameter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

/**
 * Notification mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface NotificationMapper {

    /**
     * Maps push notification request to entity model.
     *
     * @param userPushNotificationRequest - user push notification request
     * @return notification entity
     */
    NotificationEntity map(UserPushNotificationRequest userPushNotificationRequest);

    /**
     * Maps notification parameters
     *
     * @param userPushNotificationRequest - user push notification request
     * @param notificationEntity          - notification entity
     */
    @AfterMapping
    default void mapParameters(UserPushNotificationRequest userPushNotificationRequest,
                               @MappingTarget NotificationEntity notificationEntity) {
        if (!CollectionUtils.isEmpty(userPushNotificationRequest.getAdditionalProperties())) {
            var parameters = userPushNotificationRequest.getAdditionalProperties().entrySet()
                    .stream()
                    .map(entry -> {
                        var notificationParameter = new NotificationParameter();
                        notificationParameter.setName(entry.getKey());
                        notificationParameter.setValue(entry.getValue());
                        return notificationParameter;
                    })
                    .collect(Collectors.toList());
            notificationEntity.setParameters(parameters);
        }
    }
}
