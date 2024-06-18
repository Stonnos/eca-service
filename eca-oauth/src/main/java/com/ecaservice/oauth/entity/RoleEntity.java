package com.ecaservice.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Role persistence entity.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@Entity
@Table(name = "role_entity")
public class RoleEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Role name
     */
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    /**
     * Role description
     */
    private String description;
}
