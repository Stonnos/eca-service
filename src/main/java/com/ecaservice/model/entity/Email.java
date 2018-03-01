package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Email persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "email")
public class Email {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Source mail
     */
    private String sender;

    /**
     * Target email
     */
    private String receiver;

    /**
     * Email subject
     */
    private String subject;

    /**
     * Email text
     */
    @Column(columnDefinition = "text")
    private String message;

    /**
     * Email save date
     */
    @Column(name = "save_date")
    private LocalDateTime saveDate;

    /**
     * Email successful sent date
     */
    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    /**
     * Is send?
     */
    private boolean sent;

    /**
     * Error reason
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Linked experiment
     */
    @OneToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

}
