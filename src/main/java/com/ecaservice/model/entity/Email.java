package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    private String sender;
    private String receiver;
    private String subject;

    @Column(columnDefinition = "text")
    private String message;

    @Column(name = "save_date")
    private LocalDateTime saveDate;

}
