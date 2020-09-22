package com.ecaservice.oauth.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * User photo persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "user_photo")
public class UserPhoto {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * User photo file name
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * User photo extension
     */
    @Column(name = "file_extension")
    private String fileExtension;

    /**
     * User photo byte array
     */
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] photo;

    /**
     * User entity
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity userEntity;
}
