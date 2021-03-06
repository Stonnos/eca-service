package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;
import weka.core.Instances;

/**
 * Interface for saving data file into database.
 *
 * @author Roman Batygin
 */
public interface StorageService {

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    Page<InstancesEntity> getNextPage(PageRequestDto pageRequestDto);

    /**
     * Saves training data file into database.
     *
     * @param instances - instances
     * @param tableName - table name
     */
    InstancesEntity saveData(Instances instances, String tableName);

    /**
     * Renames data with specified id.
     *
     * @param id      - instances id
     * @param newName - new instances name
     * @return old table name
     */
    String renameData(long id, String newName);

    /**
     * Deletes data with specified id.
     *
     * @param id - instances id
     * @return deleted table name
     */
    String deleteData(long id);
}
