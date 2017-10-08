package com.ecaservice.service.experiment.impl;

import com.ecaservice.service.experiment.DataService;
import eca.data.DataLoader;
import eca.data.DataSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;

@Service
public class DataServiceImpl implements DataService {

    private final DataSaver dataSaver;
    private final DataLoader dataLoader;

    @Autowired
    public DataServiceImpl(DataSaver dataSaver, DataLoader dataLoader) {
        this.dataSaver = dataSaver;
        this.dataLoader = dataLoader;
    }

    @Override
    public void save(File file, Instances data) throws Exception {
        dataSaver.saveData(file, data);
    }

    @Override
    public Instances load(File file) throws Exception {
        return dataLoader.getDataSet(file);
    }
}
