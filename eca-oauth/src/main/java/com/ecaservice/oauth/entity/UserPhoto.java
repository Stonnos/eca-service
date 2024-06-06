package com.ecaservice.oauth.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
    @Basic(fetch = FetchType.LAZY)
    @Lob
    //TODO fix user photo type
  //  @Type(v = "org.hibernate.type.BinaryType")
    private byte[] photo;

    /**
     * User entity
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity userEntity;
}
