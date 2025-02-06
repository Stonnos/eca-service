package com.ecaservice.core.filter.cache;

import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * Page request key generator for caching.
 *
 * @author Roman Batygin
 */
@Slf4j
public class PageRequestKeyGenerator implements KeyGenerator {

    private static final String DELIMITER = ":";

    @Override
    public Object generate(Object target, Method method, Object... params) {
        var pageRequests = Stream.of(params)
                .filter(PageRequestDto.class::isInstance)
                .map(PageRequestDto.class::cast)
                .toList();
        if (pageRequests.size() != 1) {
            throw new IllegalStateException(
                    String.format("Expected only one argument of type [%s] to generate cache key",
                            PageRequestDto.class.getSimpleName()));
        }
        String key = getCacheKey(pageRequests.getFirst());
        log.debug("Page request cache key: {}", key);
        return key;
    }

    private String getCacheKey(PageRequestDto pageRequestDto) {
        StringBuilder stringBuilder = new StringBuilder(DELIMITER);
        if (StringUtils.isNotEmpty(pageRequestDto.getSearchQuery())) {
            stringBuilder.append(pageRequestDto.getSearchQuery());
        }
        if (!CollectionUtils.isEmpty(pageRequestDto.getFilters())) {
            stringBuilder.append(DELIMITER);
            stringBuilder.append(pageRequestDto.getFilters());
        }
        return stringBuilder.toString();
    }
}
