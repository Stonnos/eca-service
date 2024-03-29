package com.ecaservice.data.storage.service;

import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.web.dto.model.AttributeDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.file.model.InstancesModel;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import weka.core.Instances;

import java.util.List;

import static com.ecaservice.data.storage.dictionary.FilterDictionaries.INSTANCES_TEMPLATE;

/**
 * Interface for saving data file into database.
 *
 * @author Roman Batygin
 */
@Validated
public interface StorageService {

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    Page<InstancesEntity> getNextPage(
            @ValidPageRequest(filterTemplateName = INSTANCES_TEMPLATE) PageRequestDto pageRequestDto);

    /**
     * Saves training data file into database.
     *
     * @param instances    - instances
     * @param relationName - relation name
     */
    InstancesEntity saveData(Instances instances, String relationName);

    /**
     * Gets instances by id.
     *
     * @param id - instances id
     * @return instances entity
     */
    InstancesEntity getById(long id);

    /**
     * Renames data with specified id.
     *
     * @param id      - instances id
     * @param newName - new instances name
     * @return old instances name
     */
    String renameData(long id, String newName);

    /**
     * Deletes data with specified id.
     *
     * @param id - instances id
     * @return deleted instances name
     */
    String deleteData(long id);

    /**
     * Gets instances data rows.
     *
     * @param id -  instances id
     * @return instances data rows
     */
    PageDto<List<String>> getData(long id, PageRequestDto pageRequestDto);

    /**
     * Gets instances data.
     *
     * @param instancesEntity -  instances entity
     * @return instances data
     */
    Instances getInstances(InstancesEntity instancesEntity);

    /**
     * Gets valid instances model with selected attributes and assigned class attribute.
     * Valid instances is:
     * 1. Selected attributes number is greater than or equal to 2
     * 2. Class attribute is selected
     * 3. Class values number in table is greater than or equal to 2
     *
     * @param instancesEntity - instances entity
     * @return instances model object
     */
    InstancesModel getValidInstancesModel(InstancesEntity instancesEntity);

    /**
     * Gets instances attributes.
     *
     * @param id - instances id
     * @return instances attributes
     */
    List<AttributeDto> getAttributes(long id);

    /**
     * Sets class attribute for specified instances.
     *
     * @param classAttributeId - class attribute id
     * @return attribute entity
     */
    AttributeEntity setClassAttribute(long classAttributeId);

    /**
     * Selects all attributes for specified instances.
     *
     * @param id - instances id
     * @return instances entity
     */
    InstancesEntity selectAllAttributes(long id);
}
