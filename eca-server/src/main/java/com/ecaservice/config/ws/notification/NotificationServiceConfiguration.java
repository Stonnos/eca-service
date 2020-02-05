package com.ecaservice.config.ws.notification;

import com.ecaservice.config.ws.AbstractWebServiceConfiguration;
import com.ecaservice.dto.mail.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Notification web - service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(MailConfig.class)
@RequiredArgsConstructor
public class NotificationServiceConfiguration extends AbstractWebServiceConfiguration {

    private final MailConfig mailConfig;

    @Bean(name = "notificationMarshaller")
    @Override
    public Jaxb2Marshaller jaxb2Marshaller() {
        return super.jaxb2Marshaller();
    }

    @Profile("!docker")
    @Bean(name = "notificationWebServiceTemplate")
    @Override
    public WebServiceTemplate webServiceTemplate() {
        return super.webServiceTemplate();
    }

    @Profile("docker")
    @Bean(name = "notificationWebServiceTemplate")
    @Override
    public WebServiceTemplate sslWebServiceTemplate() throws Exception {
        return super.sslWebServiceTemplate();
    }

    @Override
    protected String getContextPath() {
        return EmailRequest.class.getPackage().getName();
    }

    @Override
    protected Resource getTrustStore() {
        return mailConfig.getSsl().getTrustStore();
    }

    @Override
    protected String getTrustStorePassword() {
        return mailConfig.getSsl().getTrustStorePassword();
    }
}
