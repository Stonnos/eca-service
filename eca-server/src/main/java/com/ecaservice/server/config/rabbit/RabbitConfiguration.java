package com.ecaservice.server.config.rabbit;

import com.ecaservice.rabbit.config.CoreRabbitConfiguration;
import com.ecaservice.server.mq.listener.CustomErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
@EnableConfigurationProperties(QueueConfig.class)
@Import(CoreRabbitConfiguration.class)
@RequiredArgsConstructor
public class RabbitConfiguration implements RabbitListenerConfigurer {

    public static final String ECA_RABBIT_LISTENER_CONTAINER_FACTORY = "ecaRabbitListenerContainerFactory";
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
     * Creates rabbit listener container factory bean.
     *
     * @param configurer                   - simple rabbit listener container configurer
     * @param connectionFactory            - connection factory
     * @param jackson2JsonMessageConverter - jackson 2 message converter
     * @param customErrorHandler           - custom error handler
     * @return rabbit listener container factory bean
     */
    @Bean(ECA_RABBIT_LISTENER_CONTAINER_FACTORY)
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter,
            CustomErrorHandler customErrorHandler) {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(containerFactory, connectionFactory);
        containerFactory.setMessageConverter(jackson2JsonMessageConverter);
        containerFactory.setErrorHandler(customErrorHandler);
        containerFactory.setDefaultRequeueRejected(false);
        return containerFactory;
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
