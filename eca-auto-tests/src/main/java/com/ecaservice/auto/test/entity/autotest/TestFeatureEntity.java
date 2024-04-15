package com.ecaservice.auto.test.entity.autotest;

import lombok.Data;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "auto_tests_job_id", nullable = false)
    private AutoTestsJobEntity job;
}
