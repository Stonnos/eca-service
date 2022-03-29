package com.ecaservice.auto.test.entity.autotest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
