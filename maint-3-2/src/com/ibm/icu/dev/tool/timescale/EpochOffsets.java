/*
 *************************************************************************
 * Copyright (C) 2004, International Business Machines Corporation and   *
 * others. All Rights Reserved.                                          *
 *************************************************************************
 *
 */

package com.ibm.icu.dev.tool.timescale;

import java.util.Locale;

import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.SimpleTimeZone;
import com.ibm.icu.util.TimeZone;

/**
 * This tool caluclates the numeric values of the epoch offsets
 * used in UniversalTimeScale.
 * 
 * @see com.ibm.icu.util.UniversalTimeScale
 * 
 *@draft ICU 3.2 
 */
public class EpochOffsets
{

    /**
     * The default constructor.
     * 
     * @draft ICU 3.2
     */
    public EpochOffsets()
    {
    }

    private static final long ticks        = 1;
    private static final long microseconds = ticks * 10;
    private static final long milliseconds = microseconds * 1000;
    private static final long seconds      = milliseconds * 1000;
    private static final long minutes      = seconds * 60;
    private static final long hours        = minutes * 60;
    private static final long days         = hours * 24;

    private static int[][] epochDates = {
            {   1, Calendar.JANUARY,   1},
            {1970, Calendar.JANUARY,   1},
            {1601, Calendar.JANUARY,   1},
            {1904, Calendar.JANUARY,   1},
            {2001, Calendar.JANUARY,   1},
            {1899, Calendar.DECEMBER, 31},
            {1900, Calendar.MARCH,     1}
    };
    
    /**
     * The <code>main()</code> method calculates the epoch offsets used by the
     * <code>UniversalTimeScale</code> class.
     * 
     * The calculations are done using an ICU <code>Calendar</code> object. The first step is
     * to calculate the Universal Time Scale's epoch date. Then the epoch offsets are calculated
     * by calculating each epoch date, subtracting the universal epoch date from it, and converting
     * that value to ticks.
     * 
     * @param args - the command line arguments.
     * 
     * @draft ICU 3.2
     */
    public static void main(String[] args)
    {
        TimeZone utc = new SimpleTimeZone(0, "UTC");
        Calendar cal = Calendar.getInstance(utc, Locale.ENGLISH);
        MessageFormat fmt = new MessageFormat("{0, date, full} {0, time, full} = {1}");
        Object arguments[] = {cal, null};
        
        System.out.println("Epoch offsets:");
        
        // January 1, 0001 00:00:00 is the universal epoch date...
        cal.set(1, Calendar.JANUARY, 1, 0, 0, 0);
        
        long universalEpoch = cal.getTimeInMillis();
        
        for (int i = 0; i < epochDates.length; i += 1) {
            int[] date = epochDates[i];
            
            cal.set(date[0], date[1], date[2]);
            
            long millis = cal.getTimeInMillis();
            
            arguments[1] = Long.toString((millis - universalEpoch) * milliseconds);
            
            System.out.println(fmt.format(arguments));
         }
    }
}