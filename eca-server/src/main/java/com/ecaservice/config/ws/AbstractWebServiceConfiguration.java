package com.ecaservice.config.ws;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;

import javax.net.ssl.SSLContext;
import java.util.Optional;

/**
 * Abstract configuration for external web - service.
 *
 * @author Roman Batygin
 */
public abstract class AbstractWebServiceConfiguration {

    /**
     * Creates jaxb2 marshaller.
     *
     * @return jaxb2 marshaller
     */
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(getContextPath());
        return marshaller;
    }

    /**
     * Creates web service template.
     *
     * @return web service template
     */
    public WebServiceTemplate webServiceTemplate() throws Exception {
        Jaxb2Marshaller jaxb2Marshaller = jaxb2Marshaller();
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate(jaxb2Marshaller, jaxb2Marshaller);
        HttpComponentsClientHttpRequestFactory requestFactory = httpComponentsClientHttpRequestFactory();
        ClientHttpRequestMessageSender clientHttpRequestMessageSender =
                new ClientHttpRequestMessageSender(requestFactory);
        webServiceTemplate.setMessageSender(clientHttpRequestMessageSender);
        return webServiceTemplate;
    }

    /**
     * Gets context path (package with classes generated from xsd schema).
     *
     * @return context path
     */
    protected abstract String getContextPath();

    /**
     * Gets trust store resource.
     *
     * @return trust store resource
     */
    protected Resource getTrustStore() {
        return null;
    }

    /**
     * Gets trust store password.
     *
     * @return trust store password
     */
    protected String getTrustStorePassword() {
        return null;
    }

    /**
     * Trust self signed certificate?
     *
     * @return {@code true} if trust self signed certificate
     */
    protected boolean isTrustSelfSigned() {
        return false;
    }

    private SSLContext sslContext() throws Exception {
        if (isTrustSelfSigned()) {
            return SSLContextBuilder.create().loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE).build();
        } else {
            char[] password = Optional.ofNullable(getTrustStorePassword()).map(String::toCharArray).orElse(null);
            return SSLContextBuilder.create().loadTrustMaterial(getTrustStore().getURL(), password).build();
        }
    }

    private HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() throws Exception {
        PoolingHttpClientConnectionManager connectionManager = poolingHttpClientConnectionManager();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .build());
        return requestFactory;
    }

    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() throws Exception {
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("https", new SSLConnectionSocketFactory(sslContext(),
                                NoopHostnameVerifier.INSTANCE))
                        .register("http", PlainConnectionSocketFactory.getSocketFactory()).build();
        return new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    }
}