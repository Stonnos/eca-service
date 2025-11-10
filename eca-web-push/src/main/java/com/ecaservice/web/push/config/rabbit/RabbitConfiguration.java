package com.ecaservice.web.push.config.rabbit;

import com.ecaservice.rabbit.config.RabbitListenerConfiguration;
import com.ecaservice.web.push.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
@ConditionalOnProperty(value = "app.rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class RabbitConfiguration extends RabbitListenerConfiguration {

    public final AppProperties appProperties;

    /**
     * Creates push event queue bean.
     *
     * @return push event queue bean
     */
    @Bean
    public Queue pushEventQueue() {
        return QueueBuilder.durable(appProperties.getRabbit().getQueueName()).build();
    }

    /**
     * Creates push event queue bindings bean.
     *
     * @return push event queue bindings bean
     */
    @Bean
    public Binding bindingPushEventQueue() {
        return BindingBuilder.bind(pushEventQueue()).to(DirectExchange.DEFAULT).withQueueName();
    }

    /**
     * Creates rabbit listener container factory bean.
     *
     * @param configurer        - simple rabbit listener container configurer
     * @param connectionFactory - connection factory
     * @return rabbit listener container factory bean
     */
    @Override
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        return super.rabbitListenerContainerFactory(configurer, connectionFactory);
    }

}
