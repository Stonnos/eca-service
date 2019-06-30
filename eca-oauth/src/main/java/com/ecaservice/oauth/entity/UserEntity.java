package com.ecaservice.oauth.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * User persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "user_entity")
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * User login
     */
    private String login;

    /**
     * User password
     */
    private String password;

    /**
     * Creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * User email
     */
    private String email;

    /**
     * User first name
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * User roles
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;

}
