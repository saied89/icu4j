// HolidayBundle_en_GB

package com.ibm.util.resources;

import com.ibm.util.*;
import java.util.Calendar;
import java.util.ListResourceBundle;

public class HolidayBundle_en_GB extends ListResourceBundle
{
    static private final Holiday[] fHolidays = {
        SimpleHoliday.NEW_YEARS_DAY,
        SimpleHoliday.MAY_DAY,
        new SimpleHoliday(Calendar.MAY,        31, -Calendar.MONDAY,    "Spring Holiday"),
        new SimpleHoliday(Calendar.AUGUST,     31, -Calendar.MONDAY,    "Summer Bank Holiday"),
        SimpleHoliday.CHRISTMAS,
        SimpleHoliday.BOXING_DAY,
        new SimpleHoliday(Calendar.DECEMBER,   31, -Calendar.MONDAY,    "Christmas Holiday"),

        // Easter and related holidays
        EasterHoliday.GOOD_FRIDAY,
        EasterHoliday.EASTER_SUNDAY,
        EasterHoliday.EASTER_MONDAY,
    };
    static private final Object[][] fContents = {
        { "holidays",   fHolidays },

        { "Labor Day",  "Labour Day" },
    };
    public synchronized Object[][] getContents() { return fContents; }
};
