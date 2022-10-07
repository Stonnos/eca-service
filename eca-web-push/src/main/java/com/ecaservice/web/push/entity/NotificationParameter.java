package com.ecaservice.web.push.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Notification parameter persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "notification_parameter")
public class NotificationParameter {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Parameter name
     */
    @Column(name = "parameter_name", nullable = false)
    private String name;

    /**
     * Parameter value
     */
    @Column(name = "parameter_value", nullable = false)
    private String value;
}
