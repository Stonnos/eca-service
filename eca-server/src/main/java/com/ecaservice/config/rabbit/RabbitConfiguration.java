package com.ecaservice.config.rabbit;

import com.ecaservice.rabbit.config.CoreRabbitConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

/**
 * Rabbit MQ configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(QueueConfig.class)
@Import(CoreRabbitConfiguration.class)
@RequiredArgsConstructor
public class RabbitConfiguration implements RabbitListenerConfigurer {

    private final QueueConfig queueConfig;

    /**
     * Creates evaluation request queue bean.
     *
     * @return evaluation request queue bean
     */
    @Bean
    public Queue evaluationRequestQueue() {
        return QueueBuilder.durable(queueConfig.getEvaluationRequestQueue()).build();
    }

    /**
     * Creates evaluation request queue bindings bean.
     *
     * @return evaluation request queue bindings bean
     */
    @Bean
    public Binding bindingEvaluationRequestQueue() {
        return BindingBuilder.bind(evaluationRequestQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }

    /**
     * Creates evaluation optimizer request queue bean.
     *
     * @return evaluation optimizer request queue bean
     */
    @Bean
    public Queue evaluationOptimizerRequestQueue() {
        return QueueBuilder.durable(queueConfig.getEvaluationOptimizerRequestQueue()).build();
    }

    /**
     * Creates evaluation optimizer request queue bindings bean.
     *
     * @return evaluation optimizer request queue bindings bean
     */
    @Bean
    public Binding bindingEvaluationOptimizerQueue() {
        return BindingBuilder.bind(evaluationOptimizerRequestQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }

    /**
     * Creates experiment request queue bean.
     *
     * @return experiment request queue bean
     */
    @Bean
    public Queue experimentRequestQueue() {
        return QueueBuilder.durable(queueConfig.getExperimentRequestQueue()).build();
    }

    /**
     * Creates experiment request queue bindings bean.
     *
     * @return experiment request queue bindings bean
     */
    @Bean
    public Binding bindingExperimentRequestQueue() {
        return BindingBuilder.bind(experimentRequestQueue()).to(DirectExchange.DEFAULT).withQueueName();
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
