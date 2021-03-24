package com.ecaservice.ers.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;

/**
 * @author Roman Batygin
 */
@Data
public class InputOptionsMap {

    @Valid
    private List<Entry> entry;

    @Data
    public static class Entry {

        @NotBlank
        @Size(max = MAX_LENGTH_255)
        private String key;

        @NotBlank
        @Size(max = MAX_LENGTH_255)
        private String value;
    }
}
