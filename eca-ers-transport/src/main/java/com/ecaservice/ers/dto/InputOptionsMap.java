package com.ecaservice.ers.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Roman Batygin
 */
@Data
public class InputOptionsMap {

    private List<Entry> entry;

    @Data
    public static class Entry {
        
        private String key;
        private String value;
    }
}
