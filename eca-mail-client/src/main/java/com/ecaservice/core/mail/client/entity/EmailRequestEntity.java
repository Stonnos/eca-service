package com.ecaservice.core.mail.client.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Email request entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "email_request_entity")
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
    @Column(nullable = false)
    private int priority;

    /**
     * Request created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Template variables in json format
     */
    @Column(name = "variables_json")
    private String variablesJson;

    /**
     * Is template variables encrypted?
     */
    private boolean encrypted;
}
