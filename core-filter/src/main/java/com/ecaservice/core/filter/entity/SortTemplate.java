package com.ecaservice.core.filter.entity;

import lombok.Data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Sort template persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "sort_template")
@NamedEntityGraph(name = "sortTemplates",
        attributeNodes = {
                @NamedAttributeNode(value = "sortFields")
        }
)
public class SortTemplate {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Template type
     */
    @Column(name = "template_type", nullable = false, unique = true)
    private String templateType;

    /**
     * Sort fields list
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sort_template_id", nullable = false)
    private List<SortField> sortFields;
}
