package com.ecaservice.external.api.config.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rabbit MQ configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(QueueConfig.class)
@RequiredArgsConstructor
public class RabbitConfiguration {

    private final QueueConfig queueConfig;

    /**
     * Creates jackson 2 json message converter bean.
     *
     * @return jackson 2 json message converter bean
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Creates rabbit template bean.
     *
     * @param connectionFactory - connection factory
     * @return rabbit template bean
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Creates evaluation request reply to bean.
     *
     * @return evaluation request reply to bean
     */
    @Bean
    public Queue evaluationRequestReplyToQueue() {
        return QueueBuilder.durable(queueConfig.getEvaluationRequestReplyToQueue()).build();
    }

    /**
     * Creates evaluation request reply to queue bindings bean.
     *
     * @return evaluation request reply to queue bindings bean
     */
    @Bean
    public Binding bindingEvaluationRequestReplyToQueue() {
        return BindingBuilder.bind(evaluationRequestReplyToQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }
}
