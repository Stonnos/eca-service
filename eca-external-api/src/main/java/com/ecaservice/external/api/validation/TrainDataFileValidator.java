package com.ecaservice.external.api.validation;

import com.ecaservice.external.api.validation.annotations.ValidTrainData;
import eca.data.DataFileExtension;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

/**
 * Train data file validator.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class TrainDataFileValidator implements ConstraintValidator<ValidTrainData, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        String fileName = multipartFile.getOriginalFilename();
        if (!StringUtils.isEmpty(fileName)) {
            String extension = FilenameUtils.getExtension(fileName);
            return Stream.of(DataFileExtension.values()).anyMatch(
                    dataFileExtension -> dataFileExtension.getExtension().equals(extension));
        }
        return false;
    }
}
