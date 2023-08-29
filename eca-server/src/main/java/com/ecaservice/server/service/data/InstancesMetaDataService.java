package com.ecaservice.server.service.data;

import com.ecaservice.server.model.data.InstancesMetaDataModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesMetaDataService {

    private final DataLoaderClient dataLoaderClient;

    public InstancesMetaDataModel getInstancesMetaData(String uuid) {
        //TODO impl method
        return null;
    }
}
