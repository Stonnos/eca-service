package com.ecaservice.user.profile.options.cache.client.config.rabbit;

import com.ecaservice.user.profile.options.cache.client.config.UserProfileOptionsCacheClientProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User profile options cache client rabbit MQ configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserProfileOptionsCacheClientRabbitConfiguration {

    public static final String USER_PROFILE_OPTIONS_CACHE_CLIENT_RABBIT_LISTENER_CONTAINER_FACTORY =
            "userProfileOptionsCacheClientRabbitListenerContainerFactory";
    private final UserProfileOptionsCacheClientProperties userProfileOptionsCacheClientProperties;

    /**
     * Creates user profile data event fanout exchange.
     *
     * @return user profile data event fanout exchange
     */
    @Bean
    public Exchange userProfileDataEventExchange() {
        log.info("User profile data event fanout exchange [{}] has been configured",
                userProfileOptionsCacheClientProperties.getRabbit().getDataEventExchangeName());
        return new FanoutExchange(userProfileOptionsCacheClientProperties.getRabbit().getDataEventExchangeName(), true,
                false);
    }

    /**
     * Creates user profile data event queue.
     *
     * @return user profile data event queue
     */
    @Bean
    public Queue userProfileDataEventQueue() {
        log.info("User profile data event queue [{}] has been configured",
                userProfileOptionsCacheClientProperties.getRabbit().getDataEventExchangeName());
        return QueueBuilder.durable(userProfileOptionsCacheClientProperties.getRabbit().getDataEventQueue()).build();
    }

    /**
     * Creates user profile data event queue.
     *
     * @return user profile data event binding
     */
    @Bean
    public Binding bindingUserProfileDataEventQueue(Exchange userProfileDataEventExchange,
                                                    Queue userProfileDataEventQueue) {
        return BindingBuilder.bind(userProfileDataEventQueue)
                .to(userProfileDataEventExchange)
                .with(StringUtils.EMPTY)
                .noargs();
    }

    /**
     * Creates rabbit listener container factory bean.
     *
     * @param configurer        - simple rabbit listener container configurer
     * @param connectionFactory - connection factory
     * @return rabbit listener container factory bean
     */
    @Bean(USER_PROFILE_OPTIONS_CACHE_CLIENT_RABBIT_LISTENER_CONTAINER_FACTORY)
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(containerFactory, connectionFactory);
        containerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        containerFactory.setDefaultRequeueRejected(false);
        return containerFactory;
    }
}
