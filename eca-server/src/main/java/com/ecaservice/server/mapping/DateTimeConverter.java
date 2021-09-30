package com.ecaservice.server.mapping;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Date time converter.
 *
 * @author Roman Batygin
 */
@Component
public class DateTimeConverter {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Converts local date time to string in format yyy-MM-dd HH:mm:ss.
     *
     * @param localDateTime - local date time
     * @return date time string
     */
    @Named("formatLocalDateTime")
    public String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }
}
