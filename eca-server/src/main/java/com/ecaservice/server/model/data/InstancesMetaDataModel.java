package com.ecaservice.server.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstancesMetaDataModel {

    /**
     * Instances name
     */
    private String relationName;

    /**
     * Instances size
     */
    private Integer numInstances;

    /**
     * Attributes number
     */
    private Integer numAttributes;

    /**
     * Classes number
     */
    private Integer numClasses;

    /**
     * Class name
     */
    private String className;

    /**
     * Instances file MD5 hash sum
     */
    private String md5Hash;

    /**
     * Instances object path in storage
     */
    private String objectPath;
}
