package com.ecaservice.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

/**
 * Rabbit MQ configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class RabbitConfiguration implements RabbitListenerConfigurer {

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

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(validatingHandlerMethodFactory());
    }

    /**
     * Creates validation handler method factory bean.
     *
     * @return validation handler method factory bean
     */
    @Bean
    public DefaultMessageHandlerMethodFactory validatingHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setValidator(amqpValidator());
        return factory;
    }

    /**
     * Creates MQ message validator bean
     *
     * @return message validator bean
     */
    @Bean
    public OptionalValidatorFactoryBean amqpValidator() {
        return new OptionalValidatorFactoryBean();
    }

}
