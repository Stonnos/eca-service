package com.ecaservice.oauth.config.rabbit;

import com.ecaservice.oauth.config.UserProfileProperties;
import com.ecaservice.rabbit.config.CoreRabbitConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Rabbit MQ configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@Import(CoreRabbitConfiguration.class)
@RequiredArgsConstructor
public class RabbitConfiguration {

    private final UserProfileProperties userProfileProperties;

    /**
     * Creates user profile fanout exchange.
     *
     * @return user profile fanout exchange
     */
    @Bean
    public Exchange userProfileExchange() {
        log.info("User profile fanout exchange [{}] has been configured",
                userProfileProperties.getRabbit().getExchangeName());
        return new FanoutExchange(userProfileProperties.getRabbit().getExchangeName(), true, false);
    }

    /**
     * Creates user profile declarables bean.
     *
     * @return user profile declarables bean
     */
    @Bean
    public Declarables userProfileDeclarables(Exchange userProfileExchange) {
        return new Declarables(userProfileExchange);
    }
}
