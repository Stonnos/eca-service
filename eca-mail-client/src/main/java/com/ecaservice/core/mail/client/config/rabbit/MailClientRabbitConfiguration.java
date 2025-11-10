package com.ecaservice.core.mail.client.config.rabbit;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mail client rabbit mq configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@ConditionalOnProperty(value = "mail.client.enabled", havingValue = "true")
public class MailClientRabbitConfiguration {

    public static final String MAIL_CLIENT_RABBIT_TEMPLATE = "mailClientRabbitTemplate";

    /**
     * Creates rabbit template bean.
     *
     * @param connectionFactory - connection factory
     * @return rabbit template bean
     */
    @Bean(MAIL_CLIENT_RABBIT_TEMPLATE)
    public RabbitTemplate mailClientRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
