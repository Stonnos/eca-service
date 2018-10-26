package com.ecaservice.config;

import com.ecaservice.dto.mail.EmailRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Web - service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class NotificationServiceConfiguration {


    /**
     * Creates Jax2Marshaller bean.
     *
     * @return Jaxb2Marshaller bean
     */
    @Bean
    public Jaxb2Marshaller notificationMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(EmailRequest.class.getPackage().getName());
        return marshaller;
    }

    /**
     * Creates web service template bean.
     *
     * @param notificationMarshaller - Jaxb2Marshaller bean
     * @return web service template bean
     */
    @Bean
    public WebServiceTemplate notificationWebServiceTemplate(Jaxb2Marshaller notificationMarshaller) {
        return new WebServiceTemplate(notificationMarshaller, notificationMarshaller);
    }
}
