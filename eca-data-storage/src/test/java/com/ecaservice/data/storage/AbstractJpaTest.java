package com.ecaservice.data.storage;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.InstancesRepository;
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
@EnableJpaRepositories(basePackageClasses = InstancesRepository.class)
@EntityScan(basePackageClasses = InstancesEntity.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
public abstract class AbstractJpaTest {

    @BeforeEach
    final void before() throws Exception {
        deleteAll();
        init();
    }

    @AfterEach
    final void after() {
        deleteAll();
    }

    public void init() throws Exception {
    }

    public void deleteAll() {
    }
}
