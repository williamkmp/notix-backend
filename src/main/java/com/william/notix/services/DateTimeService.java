package com.william.notix.services;

import java.util.Calendar;
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
     * determine if two date is the same, ignoring the time
     *
     * @param date1 {@link Date} date 1
     * @param date2 {@link Date} date 2
     * @return {@type boolean}
     */
    public boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        return cal1.equals(cal2);
    }
}
