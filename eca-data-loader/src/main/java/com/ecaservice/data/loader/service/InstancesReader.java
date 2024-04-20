package com.ecaservice.data.loader.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Instances reader service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesReader {

    /**
     * Reads instances file to byte array.
     *
     * @param instancesFile - instances file
     * @return bute array
     * @throws IOException in case of I/O error
     */
    @NewSpan
    public byte[] readData(MultipartFile instancesFile) throws IOException {
        log.info("Starting to read instances file [{}]", instancesFile.getOriginalFilename());
        @Cleanup var inputStream = instancesFile.getInputStream();
        byte[] content = IOUtils.toByteArray(inputStream);
        log.info("Instances file [{}] has been read", instancesFile.getOriginalFilename());
        return content;
    }
}
