package com.ecaservice.web.push;

import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String FIRST_NAME = "FirstName";
    private static final String EMAIL = "test@mail.ru";

    /**
     * Creates experiment dto.
     *
     * @return experiment dto
     */
    public static ExperimentDto createExperimentDto() {
        var experimentDto = new ExperimentDto();
        experimentDto.setRequestId(UUID.randomUUID().toString());
        experimentDto.setCreationDate(LocalDateTime.now());
        experimentDto.setRequestStatus(new EnumDto());
        experimentDto.setEmail(EMAIL);
        experimentDto.setFirstName(FIRST_NAME);
        return experimentDto;
    }
}
