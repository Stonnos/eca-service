package com.ecaservice.oauth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
