package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Ers request cache entity. Used for requests resending.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "ers_request_cache")
public class ErsRequestCache {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Json request string
     */
    @Column(name = "json_request", nullable = false, columnDefinition = "text")
    private String jsonRequest;

    /**
     * Created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Linked ers request
     */
    @OneToOne
    @JoinColumn(name = "ers_request_id", nullable = false, unique = true)
    private ErsRequest ersRequest;
}
