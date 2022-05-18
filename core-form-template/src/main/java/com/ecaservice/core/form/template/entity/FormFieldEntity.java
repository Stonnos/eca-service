package com.ecaservice.core.form.template.entity;

import com.ecaservice.web.dto.model.FieldType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

/**
 * Form field template persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "form_field", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"field_name", "template_id"}, name = "form_field_name_template_id_unique_idx")})
public class FormFieldEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Field name
     */
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    /**
     * Field description
     */
    @Column(nullable = false)
    private String description;

    /**
     * Field order
     */
    @Column(name = "field_order", nullable = false)
    private int fieldOrder;

    /**
     * Field type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;

    /**
     * Min. value
     */
    @Column(name = "min_value")
    private BigDecimal minValue;

    /**
     * Min. value inclusive?
     */
    @Column(name = "min_inclusive")
    private boolean minInclusive;

    /**
     * Max. value
     */
    @Column(name = "max_value")
    private BigDecimal maxValue;

    /**
     * Max. value inclusive?
     */
    @Column(name = "max_inclusive")
    private boolean maxInclusive;

    /**
     * Max length value
     */
    @Column(name = "max_length")
    private Integer maxLength;

    /**
     * Pattern value (regular expression)
     */
    private String pattern;

    /**
     * Invalid pattern message
     */
    @Column(name = "invalid_pattern_message")
    private String invalidPatternMessage;

    /**
     * Place holder message
     */
    @Column(name = "place_holder")
    private String placeHolder;

    /**
     * Field dictionary values
     */
    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private FieldDictionary dictionary;

    /**
     * Default value
     */
    @Column(name = "default_value")
    private String defaultValue;

    /**
     * Read only?
     */
    @Column(name = "read_only")
    private boolean readOnly;

}
