package com.ecaservice.core.form.template.entity;

import com.ecaservice.web.dto.model.FieldType;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
     * Invalid required field message
     */
    @Column(name = "invalid_required_message")
    private String invalidRequiredMessage;

    /**
     * Invalid max. length message
     */
    @Column(name = "invalid_max_length_message")
    private String invalidMaxLengthMessage;

    /**
     * Placeholder message
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
     * Form template group id (used for {@link FieldType#ONE_OF_OBJECT} and  {@link FieldType#LIST_OBJECTS} field types)
     */
    @ManyToOne
    @JoinColumn(name = "form_template_group_id")
    private FormTemplateGroupEntity formTemplateGroup;

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
