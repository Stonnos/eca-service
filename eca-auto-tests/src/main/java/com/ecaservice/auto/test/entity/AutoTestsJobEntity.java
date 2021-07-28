package com.ecaservice.auto.test.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    @Column(name = "job_uuid", nullable = false)
    private String jobUuid;
}
