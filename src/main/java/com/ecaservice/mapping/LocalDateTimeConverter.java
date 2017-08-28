package com.ecaservice.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Roman Batygin
 */
@Component
public class LocalDateTimeConverter extends BidirectionalConverter<LocalDateTime, LocalDateTime> {

    @Override
    public LocalDateTime convertTo(LocalDateTime dateTime, Type<LocalDateTime> destinationType) {
        return LocalDateTime.from(dateTime);
    }

    @Override
    public LocalDateTime convertFrom(LocalDateTime dateTime, Type<LocalDateTime> destinationType) {
        return LocalDateTime.from(dateTime);
    }
}
