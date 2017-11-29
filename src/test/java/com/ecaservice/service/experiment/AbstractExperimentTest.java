package com.ecaservice.service.experiment;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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

    protected void assertList(List<Experiment> experiments) {
        assertNotNull(experiments);
        assertFalse(experiments.isEmpty());
        assertEquals(experiments.size(), 1);
    }
}
