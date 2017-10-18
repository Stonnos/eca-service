package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Abstract experiment test class.
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ExperimentMapperImpl.class, ExperimentConfig.class})
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
