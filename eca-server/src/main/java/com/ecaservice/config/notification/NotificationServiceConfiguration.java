package com.ecaservice.config.notification;

import com.ecaservice.config.MailConfig;
import com.ecaservice.dto.mail.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.SSLContext;

/**
 * Web - service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@RequiredArgsConstructor
public class NotificationServiceConfiguration {

    private final MailConfig mailConfig;

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
     * @param notificationMarshaller - jaxb2 marshaller bean
     * @return web service template bean
     * @throws Exception in case of error
     */
    @Bean
    public WebServiceTemplate notificationWebServiceTemplate(Jaxb2Marshaller notificationMarshaller) throws Exception {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate(notificationMarshaller, notificationMarshaller);
        webServiceTemplate.setMessageSender(httpComponentsMessageSender());
        return webServiceTemplate;
    }

    private HttpComponentsMessageSender httpComponentsMessageSender() throws Exception {
        return new HttpComponentsMessageSender(httpClient());
    }

    private HttpClient httpClient() throws Exception {
        return HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory()).addInterceptorFirst(
                new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor()).build();
    }

    private SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
        return new SSLConnectionSocketFactory(sslContext(), NoopHostnameVerifier.INSTANCE);
    }

    private SSLContext sslContext() throws Exception {
        return SSLContextBuilder.create().loadTrustMaterial(mailConfig.getSsl().getTrustStore().getFile(),
                mailConfig.getSsl().getTrustStorePassword().toCharArray()).build();
    }
}
