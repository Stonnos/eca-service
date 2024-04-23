package com.ecaservice.audit.config.rabbit;

import com.ecaservice.audit.config.AuditLogProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rabbit MQ configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@ConditionalOnProperty(value = "audit.rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class RabbitConfiguration {

    public final AuditLogProperties auditLogProperties;

    /**
     * Creates audit event queue bean.
     *
     * @return audit event queue bean
     */
    @Bean
    public Queue auditEventQueue() {
        return QueueBuilder.durable(auditLogProperties.getRabbit().getQueueName()).build();
    }

    /**
     * Creates audit event queue bindings bean.
     *
     * @return audit event queue bindings bean
     */
    @Bean
    public Binding bindingAuditEventQueue() {
        return BindingBuilder.bind(auditEventQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }

    /**
     * Creates rabbit listener container factory bean.
     *
     * @param configurer        - simple rabbit listener container configurer
     * @param connectionFactory - connection factory
     * @return rabbit listener container factory bean
     */
    @Bean
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
