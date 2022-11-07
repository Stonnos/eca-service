package com.ecaservice.external.api.test.entity;

import com.ecaservice.external.api.test.dto.AutoTestType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "job")
public class JobEntity extends BaseEntity {

    /**
     * Auto tests job uuid
     */
    @Column(name = "job_uuid", nullable = false)
    private String jobUuid;

    /**
     * Threads number
     */
    @Column(name = "num_threads")
    private Integer numThreads;

    /**
     * Auto test type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "auto_test_type", nullable = false)
    private AutoTestType autoTestType;
}
