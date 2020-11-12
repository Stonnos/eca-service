package com.ecaservice.external.api.service;

import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.exception.DataNotFoundException;
import com.ecaservice.external.api.exception.EntityNotFoundException;
import com.ecaservice.external.api.repository.InstancesRepository;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.DataResource;
import eca.data.file.resource.FileResource;
import eca.data.file.resource.UrlResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Instances service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesService {

    private static final String DATA_URL_PREFIX = "data://";

    private final FileDataLoader fileDataLoader;
    private final InstancesRepository instancesRepository;

    /**
     * Loads instances for specified url.
     *
     * @param url - url string
     * @return instances object
     */
    public Instances loadInstances(String url) {
        try {
            DataResource<?> dataResource = createDataResource(url);
            fileDataLoader.setSource(dataResource);
            return fileDataLoader.loadInstances();
        } catch (Exception ex) {
            throw new DataNotFoundException(ex.getMessage());
        }

    }

    private DataResource<?> createDataResource(String urlString) throws MalformedURLException {
        if (urlString.startsWith(DATA_URL_PREFIX)) {
            String dataUuid = StringUtils.substringAfter(urlString, DATA_URL_PREFIX);
            InstancesEntity instancesEntity = instancesRepository.findByUuid(dataUuid).orElseThrow(
                    () -> new EntityNotFoundException(InstancesEntity.class, dataUuid));
            return new FileResource(new File(instancesEntity.getAbsolutePath()));
        }
        return new UrlResource(new URL(urlString));
    }

}
