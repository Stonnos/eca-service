package com.ecaservice.mail.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
     * Email uuid
     */
    @Column(nullable = false)
    private String uuid;

    /**
     * Source mail
     */
    @Column(nullable = false)
    private String sender;

    /**
     * Target email
     */
    @Column(nullable = false)
    private String receiver;

    /**
     * Email subject
     */
    @Column(nullable = false)
    private String subject;

    /**
     * Email text
     */
    @Column(columnDefinition = "text", nullable = false)
    private String message;

    /**
     * Email save date
     */
    @Column(name = "save_date", nullable = false)
    private LocalDateTime saveDate;

    /**
     * Email successful sent date
     */
    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    /**
     * Email sent status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailStatus status;

    /**
     * Error reason
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Number of failed attempts to send email
     */
    @Column(name = "failed_attempts_to_sent")
    private int failedAttemptsToSent;

    /**
     * Identifier for cross - system logging
     */
    @Column(name = "tx_id")
    private String txId;

    /**
     * Delivery priority
     */
    @Column(nullable = false)
    private int priority;
}
