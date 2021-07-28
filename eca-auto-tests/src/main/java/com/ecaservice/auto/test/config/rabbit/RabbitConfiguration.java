package com.ecaservice.auto.test.config.rabbit;

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
     * Creates experiment response queue bean.
     *
     * @return experiment response queue queue bean
     */
    @Bean
    public Queue experimentResponseQueue() {
        return QueueBuilder.durable(queueConfig.getExperimentReplyToQueue()).build();
    }

    /**
     * Creates experiment response queue bindings bean.
     *
     * @return experiment response queue bindings bean
     */
    @Bean
    public Binding bindingExperimentResponseQueue() {
        return BindingBuilder.bind(experimentResponseQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }
}
