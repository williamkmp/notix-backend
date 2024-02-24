package com.william.notix.services;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DateTimeServiceImpl {

    /**
     * get current server time with offset
     * @return {@link OffsetDateTime} cuurent server date time
     */
    public OffsetDateTime getCurrentServerTime() {
        return OffsetDateTime.now();
    }
}
