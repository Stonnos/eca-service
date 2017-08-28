package com.ecaservice.dto;

import com.ecaservice.model.InputOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputOptionsList {

    private List<InputOptions> inputOptionsList;

}
