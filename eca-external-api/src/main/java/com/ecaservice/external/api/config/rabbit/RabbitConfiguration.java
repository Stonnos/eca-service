package com.ecaservice.external.api.config.rabbit;

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
     * Creates evaluation response queue bean.
     *
     * @return  evaluation response queue bean
     */
    @Bean
    public Queue evaluationResponseQueue() {
        return QueueBuilder.durable(queueConfig.getEvaluationResponseQueue()).build();
    }

    /**
     * Creates evaluation response queue bindings bean.
     *
     * @return evaluation response queue bindings bean
     */
    @Bean
    public Binding bindingEvaluationResponseQueue() {
        return BindingBuilder.bind(evaluationResponseQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }
}
