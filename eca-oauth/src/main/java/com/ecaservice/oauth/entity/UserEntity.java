package com.ecaservice.oauth.entity;

import lombok.Data;
import org.hibernate.annotations.Formula;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
    @Column(nullable = false, unique = true)
    private String login;

    /**
     * User password
     */
    @Column(nullable = false)
    private String password;

    /**
     * Creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * User email
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * User first name
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * User last name
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * User middle name
     */
    @Column(name = "middle_name", nullable = false)
    private String middleName;

    /**
     * Two factor authentication enabled?
     */
    @Column(name = "tfa_enabled")
    private boolean tfaEnabled;

    /**
     * Account locked?
     */
    private boolean locked;

    /**
     * Force change temporary password?
     */
    @Column(name = "force_change_password")
    private boolean forceChangePassword;

    /**
     * Last password change date
     */
    @Column(name = "password_change_date")
    private LocalDateTime passwordChangeDate;

    /**
     * User roles
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;

    /**
     * User full name
     */
    @Formula("concat(last_name, ' ', first_name, ' ', middle_name)")
    private String fullName;
}
