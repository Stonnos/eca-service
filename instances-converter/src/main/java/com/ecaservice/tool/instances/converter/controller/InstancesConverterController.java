package com.ecaservice.tool.instances.converter.controller;

import com.ecaservice.tool.instances.converter.service.InstancesConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InstancesConverterController {

    private final InstancesConverterService instancesConverterService;

    @GetMapping(value = "/convert-to-json")
    public void convertToJson() {
        instancesConverterService.convertInstancesToJson();
    }
}
