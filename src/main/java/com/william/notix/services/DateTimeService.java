package com.william.notix.services;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DateTimeService {

    /**
     * get current server time
     * @return {@link Date} current date time
     */
    public Date now() {
        return new Date();
    }

    /**
     * convert OffsetDateTime to server local Date
     *
     * @param dateTime {@link OffsetDateTime} offsetdatetime
     * @return {@link Date} server local date time
     */
    public Date toDate(OffsetDateTime offsetDtm) {
        return Date.from(offsetDtm.toInstant());
    }

    /**
     * convert Date to OffsetDateTime
     *
     * @param date {@link Date} datetime
     * @return {@link OffsetDateTime} datetime with offset
     */
    public OffsetDateTime toOffsetDtm(Date date) {
        Instant dateInstance = date.toInstant();
        return dateInstance.atOffset(ZoneOffset.UTC);
    }
}
