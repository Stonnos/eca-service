package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.test.dto.AbstractAutoTestDto;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.mapping.BaseAutoTestRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Auto test request adapter.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class AutoTestRequestAdapter {

    private final List<BaseAutoTestRequestMapper> autoTestRequestMappers;

    /**
     * Maps auto test entity to dto model.
     *
     * @param autoTestEntity - auto test entity
     * @return auto test dto model
     */
    @SuppressWarnings("unchecked")
    public AbstractAutoTestDto proceed(AutoTestEntity autoTestEntity) {
        var mapper = autoTestRequestMappers.stream()
                .filter(m -> m.canMap(autoTestEntity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle auto test [%s]", autoTestEntity.getClass().getSimpleName())));
        return mapper.map(autoTestEntity);
    }
}
