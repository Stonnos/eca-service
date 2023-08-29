package com.ecaservice.server.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Instances;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesLoaderService {

    private final InstancesMetaDataService instancesMetaDataService;


    public Instances loadInstances(String uuid) {
        //TODO impl method
        return null;
    }
}
