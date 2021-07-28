package com.ecaservice.auto.test.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Base entity model.
 *
 * @author Roman Batygin
 */
@Data
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Created date
     */
    private LocalDateTime created;

    /**
     * Started date
     */
    private LocalDateTime started;

    /**
     * Finished date
     */
    private LocalDateTime finished;

    /**
     * Test execution status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "execution_status")
    private ExecutionStatus executionStatus;

    /**
     * Details string
     */
    @Column(columnDefinition = "text")
    private String details;
}
