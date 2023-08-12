package com.ecaservice.core.filter.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    @Column(name = "template_type", nullable = false)
    private String templateType;

    /**
     * Sort fields list
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sort_template_id", nullable = false)
    private List<SortField> sortFields;
}
