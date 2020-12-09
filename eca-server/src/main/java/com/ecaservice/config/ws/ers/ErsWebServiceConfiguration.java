package com.ecaservice.config.ws.ers;

import com.ecaservice.config.ws.AbstractWebServiceConfiguration;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.Optional;

/**
 * ERS web - service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(ErsConfig.class)
@RequiredArgsConstructor
public class ErsWebServiceConfiguration extends AbstractWebServiceConfiguration {

    private final ErsConfig ersConfig;

    @Bean(name = "ersMarshaller")
    @Override
    public Jaxb2Marshaller jaxb2Marshaller() {
        return super.jaxb2Marshaller();
    }

    @Bean(name = "ersWebServiceTemplate")
    @Override
    public WebServiceTemplate webServiceTemplate() throws Exception {
        return super.webServiceTemplate();
    }

    @Override
    protected String getContextPath() {
        return EvaluationResultsRequest.class.getPackage().getName();
    }

    @Override
    protected Resource getTrustStore() {
        return Optional.ofNullable(ersConfig.getSsl()).map(ErsConfig.SslConfig::getTrustStore).orElse(null);
    }

    @Override
    protected boolean isTrustSelfSigned() {
        return Optional.ofNullable(ersConfig.getSsl()).map(ErsConfig.SslConfig::getTrustSelfSigned).orElse(false);
    }

    @Override
    protected String getTrustStorePassword() {
        return Optional.ofNullable(ersConfig.getSsl()).map(ErsConfig.SslConfig::getTrustStorePassword).orElse(null);
    }
}