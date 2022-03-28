package com.ecaservice.auto.test.entity.autotest;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Test feature persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "test_feature")
public class TestFeatureEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Test feature type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "test_feature", nullable = false)
    private TestFeature testFeature;

    /**
     * Auto tests job
     */
    @ManyToOne
    @JoinColumn(name = "auto_tests_job_id", nullable = false)
    private AutoTestsJobEntity job;
}
