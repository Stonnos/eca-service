package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Data list page dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Data list page dto")
public class DataListPageDto extends PageDto<List<String>> {
}
