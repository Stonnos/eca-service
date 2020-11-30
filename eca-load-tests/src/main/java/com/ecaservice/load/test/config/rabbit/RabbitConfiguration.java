package com.ecaservice.load.test.config.rabbit;

import com.ecaservice.rabbit.config.CoreRabbitConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Rabbit MQ configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(CoreRabbitConfiguration.class)
@EnableConfigurationProperties(QueueConfig.class)
@RequiredArgsConstructor
public class RabbitConfiguration {

    private final QueueConfig queueConfig;

    /**
     * Creates response queue bean.
     *
     * @return response queue queue bean
     */
    @Bean
    public Queue responseQueue() {
        return QueueBuilder.durable(queueConfig.getReplyToQueue()).build();
    }

    /**
     * Creates response queue bindings bean.
     *
     * @return response queue bindings bean
     */
    @Bean
    public Binding bindingResponseQueue() {
        return BindingBuilder.bind(responseQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }
}
