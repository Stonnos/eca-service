package com.ecaservice.external.api;

import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Abstract class with JPA test configuration.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureDataJpa
@EnableJpaRepositories(basePackageClasses = EcaRequestRepository.class)
@EntityScan(basePackageClasses = EcaRequestEntity.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application-test.properties")
public abstract class AbstractJpaTest {

    @BeforeEach
    final void before() {
        deleteAll();
        init();
    }

    @AfterEach
    final void after() {
        deleteAll();
    }

    public void init() {
    }

    public void deleteAll() {
    }
}
