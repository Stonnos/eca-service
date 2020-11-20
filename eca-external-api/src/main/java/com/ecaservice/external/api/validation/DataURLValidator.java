package com.ecaservice.external.api.validation;

import com.ecaservice.external.api.dto.annotations.DataURL;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;

import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;

/**
 * Data url validation.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class DataURLValidator implements ConstraintValidator<DataURL, String> {

    @Override
    public boolean isValid(String url, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        if (url.startsWith(DATA_URL_PREFIX)) {
            return true;
        }
        try {
            new URL(url);
        } catch (MalformedURLException ex) {
            return false;
        }
        return true;
    }
}