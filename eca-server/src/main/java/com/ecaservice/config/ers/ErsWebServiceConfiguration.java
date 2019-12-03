package com.ecaservice.config.ers;

import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(ErsConfig.class)
public class ErsWebServiceConfiguration {

    /**
     * Creates Jax2Marshaller bean.
     *
     * @return Jaxb2Marshaller bean
     */
    @Bean
    public Jaxb2Marshaller ersMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(EvaluationResultsRequest.class.getPackage().getName());
        return marshaller;
    }

    /**
     * Creates web service template bean.
     *
     * @param ersMarshaller - Jaxb2Marshaller bean
     * @return web service template bean
     */
    @Bean
    public WebServiceTemplate ersWebServiceTemplate(Jaxb2Marshaller ersMarshaller) {
        return new WebServiceTemplate(ersMarshaller, ersMarshaller);
    }
}
