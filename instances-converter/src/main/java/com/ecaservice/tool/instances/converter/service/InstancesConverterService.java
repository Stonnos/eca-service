package com.ecaservice.tool.instances.converter.service;

import com.ecaservice.tool.instances.converter.entity.InstancesInfo;
import com.ecaservice.tool.instances.converter.repository.InstancesInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesConverterService {

    private final ObjectMapper objectMapper;
    private final InstancesInfoRepository instancesInfoRepository;

    private int success;
    private int failed;

    public void convertInstancesToJson() {
        success = 0;
        failed = 0;
        log.info("Starting to converts instances to json format");
        List<InstancesInfo> instancesInfos = instancesInfoRepository.findAll();
        instancesInfos.forEach(this::convert);
        log.info("All instances has been converted to json format: success [{}], failed [{}]", success, failed);
    }

    @SneakyThrows
    private void convert(InstancesInfo instancesInfo) {
        log.info("Starting to convert instances [{}] to json", instancesInfo.getId());
        byte[] xmlBytes = instancesInfo.getStructure();
        String xml = new String(xmlBytes, StandardCharsets.UTF_8.name());
        try {
            InstancesModel instancesModel = unmarshal(xml);
            String json = toJson(instancesModel);
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8.name());
            instancesInfo.setDataMd5Hash(DigestUtils.md5DigestAsHex(jsonBytes));
            instancesInfo.setStructure(jsonBytes);
            instancesInfoRepository.save(instancesInfo);
            log.info("Instances [{}] has been converted to json with hash [{}]", instancesInfo.getId(),
                    instancesInfo.getDataMd5Hash());
            success++;
        } catch (Exception ex) {
            log.error("There was an error while instances [{}] conversion: {}", instancesInfo.getId(), ex.getMessage());
            failed++;
        }
    }

    private InstancesModel unmarshal(String xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(InstancesModel.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        @Cleanup StringReader reader = new StringReader(xml);
        return (InstancesModel) unmarshaller.unmarshal(reader);
    }

    private String toJson(InstancesModel instancesModel) throws JsonProcessingException {
        return objectMapper.writeValueAsString(instancesModel);
    }
}
