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

    //@Bean(name = "ersWebServiceTemplate")
   // @Override
   // public WebServiceTemplate webServiceTemplate() {
  //      return super.webServiceTemplate();
 //   }

    @Bean(name = "ersWebServiceTemplate")
    @Override
    public WebServiceTemplate sslWebServiceTemplate() throws Exception {
        return super.sslWebServiceTemplate();
    }

    @Override
    protected String getContextPath() {
        return EvaluationResultsRequest.class.getPackage().getName();
    }

    @Override
    protected Resource getTrustStore() {
        return ersConfig.getSsl().getTrustStore();
    }

    @Override
    protected String getTrustStorePassword() {
        return ersConfig.getSsl().getTrustStorePassword();
    }
}
