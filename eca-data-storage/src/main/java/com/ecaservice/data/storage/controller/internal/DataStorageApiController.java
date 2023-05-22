package com.ecaservice.data.storage.controller.internal;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Data storage internal API.
 *
 * @author Roman Batygin
 */
@Validated
@Slf4j
@Tag(name = "Data storage internal API")
@RestController
@RequestMapping("/api/internal/instances")
@RequiredArgsConstructor
public class DataStorageApiController {
}
