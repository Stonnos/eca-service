package com.ecaservice.model.entity;

import com.ecaservice.dto.mail.ResponseStatus;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "response_status")
    private ResponseStatus responseStatus;

    /**
     * Email request date
     */
    @Column(name = "request_date")
    private LocalDateTime requestDate;

    /**
     * Error message
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Linked experiment
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
