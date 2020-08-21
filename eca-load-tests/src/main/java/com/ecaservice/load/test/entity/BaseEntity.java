package com.ecaservice.load.test.entity;

import lombok.Data;

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
     * Started date
     */
    private LocalDateTime started;

    /**
     * Finished date
     */
    private LocalDateTime finished;
}
