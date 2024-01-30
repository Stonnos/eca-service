package com.ecaservice.user.profile.options.cache.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * User profile options cache client properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("user-profile.client.cache")
public class UserProfileOptionsCacheClientProperties {

    /**
     * Use user profile options cache?
     */
    private boolean enabled;

    /**
     * Rabbit properties
     */
    private RabbitProperties rabbit = new RabbitProperties();

    /**
     * Rabbit properties.
     */
    @Data
    public static class RabbitProperties {

        /**
         * User profile data event exchange name
         */
        private String dataEventExchangeName;

        /**
         * User profile data event queue name
         */
        private String dataEventQueue;
    }
}
