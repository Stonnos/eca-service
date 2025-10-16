package com.ecaservice.core.push.client.config.rabbit;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web push rabbit mq configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class WebPushClientRabbitConfiguration {

    public static final String WEB_PUSH_RABBIT_TEMPLATE = "webPushRabbitTemplate";

    /**
     * Creates rabbit template bean.
     *
     * @param connectionFactory - connection factory
     * @return rabbit template bean
     */
    @Bean(WEB_PUSH_RABBIT_TEMPLATE)
    public RabbitTemplate webPushRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
