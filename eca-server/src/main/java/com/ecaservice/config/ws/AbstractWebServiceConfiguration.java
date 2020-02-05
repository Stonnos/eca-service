package com.ecaservice.config.ws;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.SSLContext;

/**
 * Abstract configuration for external web - service.
 *
 * @author Roman Batygin
 */
public abstract class AbstractWebServiceConfiguration {

    /**
     * Creates Jax2Marshaller bean.
     *
     * @return Jaxb2Marshaller bean
     */
    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(getContextPath());
        return marshaller;
    }

    /**
     * Creates web service template bean.
     *
     * @return web service template bean
     */
    public WebServiceTemplate webServiceTemplate() {
        Jaxb2Marshaller jaxb2Marshaller = jaxb2Marshaller();
        return new WebServiceTemplate(jaxb2Marshaller, jaxb2Marshaller);
    }

    /**
     * Creates ssl web service template bean.
     *
     * @return ssl web service template bean
     * @throws Exception in case of error
     */
    public WebServiceTemplate sslWebServiceTemplate() throws Exception {
        WebServiceTemplate webServiceTemplate = webServiceTemplate();
        webServiceTemplate.setMessageSender(httpComponentsMessageSender());
        return webServiceTemplate;
    }

    /**
     * Gets context path.
     *
     * @return context path
     */
    protected abstract String getContextPath();

    /**
     * Gets trust store resource.
     *
     * @return trust store resource
     */
    protected abstract Resource getTrustStore();

    /**
     * Gets trust store password.
     *
     * @return trust store password
     */
    protected abstract String getTrustStorePassword();

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
        return SSLContextBuilder.create().loadTrustMaterial(getTrustStore().getFile(),
                getTrustStorePassword().toCharArray()).build();
    }
}
