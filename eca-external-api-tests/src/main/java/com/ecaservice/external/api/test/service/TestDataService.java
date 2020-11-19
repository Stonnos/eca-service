package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class TestDataService {

    public List<EvaluationRequestDto> getRequests() {
        return Collections.emptyList();
    }
}
