package com.ecaservice.auto.test.entity.autotest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Auto tests job persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "auto_tests_job")
public class AutoTestsJobEntity extends BaseEntity {

    /**
     * Auto tests job uuid
     */
    @Column(name = "job_uuid", nullable = false, unique = true)
    private String jobUuid;

    /**
     * Auto test type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "auto_test_type", nullable = false)
    private AutoTestType autoTestType;

    /**
     * Additional test features
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "job")
    private List<TestFeatureEntity> features;
}
