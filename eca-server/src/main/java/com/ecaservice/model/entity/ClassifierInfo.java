package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Classifier info persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_info")
public class ClassifierInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "classifier_name", nullable = false)
    private String classifierName;

    /**
     * Classifier input options list
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "classifier_info_id", nullable = false)
    private List<ClassifierInputOptions> classifierInputOptions;
}
