package com.ecaservice.mail.config.rabbit;

import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.rabbit.config.RabbitListenerConfiguration;
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
@ConditionalOnProperty(value = "mail-config.rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class RabbitConfiguration extends RabbitListenerConfiguration {

    public final MailConfig mailConfig;

    /**
     * Creates email event queue bean.
     *
     * @return email event queue bean
     */
    @Bean
    public Queue emailEventQueue() {
        return QueueBuilder.durable(mailConfig.getRabbit().getQueueName()).build();
    }

    /**
     * Creates email event queue bindings bean.
     *
     * @return email event queue bindings bean
     */
    @Bean
    public Binding bindingEmailEventQueue() {
        return BindingBuilder.bind(emailEventQueue()).to(DirectExchange.DEFAULT).withQueueName();
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
