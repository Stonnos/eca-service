package com.ecaservice.external.api.test.entity;

import com.ecaservice.test.common.model.ExecutionStatus;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
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
