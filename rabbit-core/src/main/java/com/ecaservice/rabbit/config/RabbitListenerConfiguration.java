package com.ecaservice.rabbit.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;

/**
 * Rabbit listener configuration.
 *
 * @author Roman Batygin
 */
public class RabbitListenerConfiguration {

    /**
     * Creates rabbit listener container factory bean.
     *
     * @param configurer        - simple rabbit listener container configurer
     * @param connectionFactory - connection factory
     * @return rabbit listener container factory bean
     */
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
