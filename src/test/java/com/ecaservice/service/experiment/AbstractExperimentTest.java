package com.ecaservice.service.experiment;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

/**
 * Abstract experiment test class.
 *
 * @author Roman Batygin
 */
@AutoConfigureDataJpa
@EnableJpaRepositories(basePackageClasses = ExperimentRepository.class)
@EntityScan(basePackageClasses = Experiment.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application-test.properties")
public abstract class AbstractExperimentTest {
}
