package com.ecaservice.web.push.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Notification parameter persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "notification_parameter",
        indexes = @Index(
                columnList = "id, parameter_name",
                name = "notification_parameter_id_parameter_name_unique_idx",
                unique = true
        )
)
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
