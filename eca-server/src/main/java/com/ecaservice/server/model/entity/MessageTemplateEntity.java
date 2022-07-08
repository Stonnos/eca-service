package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Message template persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "message_template")
public class MessageTemplateEntity {

    /**
     * Template id
     */
    @Id
    private String id;

    /**
     * Template text
     */
    @Column(name = "template_text", columnDefinition = "text", nullable = false)
    private String templateText;
}
