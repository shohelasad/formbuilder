package com.formreleaf.common.utils;

import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/20/15.
 */
public class DateUtils {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DateUtils.class);

    private static SimpleDateFormat sdf;
    private static SimpleDateFormat sdf2;

    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        sdf2 = new SimpleDateFormat("MM/dd/yyyy");
    }

    public static Date setTime(final Date date, final int hourOfDay, final int minute, final int second, final int ms) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY, hourOfDay);
        gc.set(Calendar.MINUTE, minute);
        gc.set(Calendar.SECOND, second);
        gc.set(Calendar.MILLISECOND, ms);

        return gc.getTime();
    }

    public static Date getDateWithoutTime() {

        return setTime(new Date(), 0, 0, 0, 0);
    }

    //convert yyyy-MM-dd'T'HH:mm:ss'Z date to MM/dd/yyyy
    public static String convert(String date) {
        try {

            if (StringUtils.isNotEmpty(date)) {

                Date parsed = sdf.parse(date);

                return sdf2.format(parsed);
            }

        } catch (ParseException e) {
            LOGGER.debug("Could not convert date '{}' to java.util Date", date);
        }
        return date;
    }
}
