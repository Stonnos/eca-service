package com.ecaservice.model.entity;

import com.ecaservice.dto.mail.ResponseStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Email request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "email_request")
public class EmailRequestEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Email request id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Response status
     */
    private ResponseStatus status;

    /**
     * Email request creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * Error message
     */
    @Lob
    @Column(name = "error_message")
    private String errorMessage;

    /**
     * Linked experiment
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;
}
