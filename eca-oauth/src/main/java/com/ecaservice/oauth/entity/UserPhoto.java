package com.ecaservice.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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
    private byte[] photo;
}
