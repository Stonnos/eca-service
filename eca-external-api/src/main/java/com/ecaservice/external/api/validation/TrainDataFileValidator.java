package com.ecaservice.external.api.validation;

import com.ecaservice.external.api.validation.annotations.ValidTrainData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static eca.data.FileUtils.isValidTrainDataFile;

/**
 * Train data file validator.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class TrainDataFileValidator implements ConstraintValidator<ValidTrainData, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        return isValidTrainDataFile(multipartFile.getOriginalFilename());
    }
}
