package com.ecaservice.core.mail.client.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static com.ecaservice.core.mail.client.util.Constraints.REQUEST_JSON_LENGTH;

/**
 * Email request entity.
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
     * Receiver
     */
    @Column(nullable = false)
    private String receiver;

    /**
     * Template code
     */
    @Column(name = "template_code", nullable = false)
    private String templateCode;

    /**
     * Delivery priority
     */
    private int priority;

    /**
     * Request created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Email request in json format
     */
    @Column(name = "request_json", length = REQUEST_JSON_LENGTH)
    private String requestJson;

    /**
     * Is template variables encrypted?
     */
    private boolean encrypted;

    /**
     * Email request status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private EmailRequestStatus requestStatus;

    /**
     * Email request id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Email request sent date
     */
    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    /**
     * Email request cache expiration date
     */
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    /**
     * Transaction id (used for cross - system logging)
     */
    @Column(name = "tx_id")
    private String txId;

    /**
     * Details string
     */
    @Column(columnDefinition = "text")
    private String details;
}
