package com.ecaservice.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rabbit MQ configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class RabbitConfiguration {

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
     * Creates evaluation request queue bean.
     *
     * @return evaluation request queue bean
     */
    @Bean
    public Queue evaluationRequestQueue() {
        return QueueBuilder.durable(Queues.EVALUATION_REQUEST_QUEUE).build();
    }

    /**
     * Creates evaluation request queue bindings bean.
     *
     * @return evaluation request queue bindings bean
     */
    @Bean
    public Binding bindingEvaluation() {
        return BindingBuilder.bind(evaluationRequestQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }
}
