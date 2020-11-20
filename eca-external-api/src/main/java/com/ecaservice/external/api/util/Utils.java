package com.ecaservice.external.api.util;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.DataFileExtension;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.stream.Stream;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String ATTACHMENT = "attachment";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Converts classifier options to json string.
     *
     * @param classifierOptions - classifier options
     * @return classifier options as json
     */
    public static String toJson(ClassifierOptions classifierOptions) {
        try {
            return OBJECT_MAPPER.writeValueAsString(classifierOptions);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Creates response with attachment.
     *
     * @param file - file attachment
     * @return response entity
     */
    public static ResponseEntity<FileSystemResource> buildAttachmentResponse(File file) {
        FileSystemResource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, resource.getFilename());
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    /**
     * Checks file existing.
     *
     * @param file - file
     * @return {@code true} if file is existing
     */
    public static boolean existsFile(File file) {
        return file != null && file.isFile();
    }

    /**
     * Validates training data extension. Must be one of xls, xlsx, csv, arff, json, xml, txt, data, docx.
     *
     * @param fileName - file name
     * @return {@code true} if file train data extension is valid
     */
    public static boolean isValidTrainData(String fileName) {
        if (!StringUtils.isEmpty(fileName)) {
            String extension = FilenameUtils.getExtension(fileName);
            return Stream.of(DataFileExtension.values()).anyMatch(
                    dataFileExtension -> dataFileExtension.getExtension().equals(extension));
        }
        return false;
    }

    /**
     * Builds response with specified fields.
     *
     * @param requestStatus - request status
     * @param <T>           - payload generic type
     * @return response object
     */
    public static <T> ResponseDto<T> buildResponse(RequestStatus requestStatus) {
        return buildResponse(requestStatus, null);
    }

    /**
     * Builds response with specified fields.
     *
     * @param requestStatus - request status
     * @param payload       - payload object
     * @param <T>           - payload generic type
     * @return response object
     */
    public static <T> ResponseDto<T> buildResponse(RequestStatus requestStatus, T payload) {
        return ResponseDto.<T>builder()
                .requestStatus(requestStatus)
                .errorDescription(requestStatus.getDescription())
                .payload(payload)
                .build();
    }
}
