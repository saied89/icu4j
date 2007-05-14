/*
 *******************************************************************************
 * Copyright (C) 2007, International Business Machines Corporation and         *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 */
package com.ibm.icu.util;
import java.util.Date;

import com.ibm.icu.impl.Grego;


/**
 * AnnualTimeZoneRule is a class used for representing a time zone
 * rule which takes effect annually.  Years used in this class are
 * all Gregorian calendar years.
 * 
 * @draft ICU 3.8
 * @provisional This API might change or be removed in a future release.
 */
public class AnnualTimeZoneRule extends TimeZoneRule {

    /**
     * The constant representing the maximum year used for designating a rule is permanent.
     */
    public static final int MAX_YEAR = Integer.MAX_VALUE;

    private final AnnualDateTimeRule dateTimeRule;
    private final int startYear;
    private final int endYear;

    private static final int MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    /**
     * Constructs a TimeZoneRule with the name, the GMT offset of its
     * standard time, the amount of daylight saving offset adjustment,
     * the annual start time rule and the start/until years.
     * 
     * @param name          The time zone name.
     * @param stdOffset     The GMT offset of its standard time in milliseconds.
     * @param dstSaving     The amount of daylight saving offset adjustment in
     *                      milliseconds.  If this ia a rule for standard time,
     *                      the value of this argument is 0.
     * @param dateTimeRule  The start date/time rule repeated annually.
     * @param startYear     The first year when this rule takes effect.
     * @param endYear       The last year when this rule takes effect.  If this
     *                      rule is effective forever in future, specify MAX_YEAR.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public AnnualTimeZoneRule(String name, int stdOffset, int dstSaving,
            AnnualDateTimeRule dateTimeRule, int startYear, int endYear) {
        super(name, stdOffset, dstSaving);
        this.dateTimeRule = dateTimeRule;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    /**
     * Gets the start date/time rule associated used by this rule.
     * 
     * @return  An AnnualDateTimeRule which represents the start date/time
     *          rule used by this time zone rule.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public AnnualDateTimeRule getRule() {
        return dateTimeRule;
    }

    /**
     * Gets the first year when this rule takes effect.
     * 
     * @return  The start year of this rule.  The year is in Gregorian calendar
     *          with 0 == 1 BCE, -1 == 2 BCE, etc.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public int getStartYear() {
        return startYear;
    }

    /**
     * Gets the end year when this rule takes effect.
     * 
     * @return  The end year of this rule (inclusive). The year is in Gregorian calendar
     *          with 0 == 1 BCE, -1 == 2 BCE, etc.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public int getEndYear() {
        return endYear;
    }

    /**
     * Gets the time when this rule takes effect in the given year.
     * 
     * @param year              The Gregorian year, with 0 == 1 BCE, -1 == 2 BCE, etc.
     * @param prevStdOffset     The offset from UTC before this rule takes effect
     *                          in milliseconds.
     * @param prevDstSaving     The amount of daylight saving offset from the
     *                          standard time.
     * 
     * @return  The time when this rule takes effect in the year, or
     *          null if this rule is not applicable in the year.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public Date getStartInYear(int year, int prevStdOffset, int prevDstSaving) {
        if (year < startYear || year > endYear) {
            return null;
        }

        long ruleDay;
        int type = dateTimeRule.getDateRuleType();

        if (type == AnnualDateTimeRule.DOM) {
            ruleDay = Grego.fieldsToDay(year, dateTimeRule.getRuleMonth(), dateTimeRule.getRuleDayOfMonth());
        } else {
            boolean after;
            if (type == AnnualDateTimeRule.DOW) {
                int weeks = dateTimeRule.getRuleWeekInMonth();
                if (weeks > 0) {
                    after = true;
                    ruleDay = Grego.fieldsToDay(year, dateTimeRule.getRuleMonth(), 1);
                    ruleDay += 7 * (weeks - 1);
                } else {
                    after = false;
                    ruleDay = Grego.fieldsToDay(year, dateTimeRule.getRuleMonth(), 
                            Grego.monthLength(year, dateTimeRule.getRuleMonth()));
                    ruleDay += 7 * (weeks + 1);
                }
            } else {
                ruleDay = Grego.fieldsToDay(year, dateTimeRule.getRuleMonth(), dateTimeRule.getRuleDayOfMonth());
                after = (type == AnnualDateTimeRule.DOM_GEQ_DOM);
            }

            int dow = Grego.dayOfWeek(ruleDay);
            int delta = dateTimeRule.getRuleDayOfWeek() - dow;
            if (after) {
                delta = delta < 0 ? delta + 7 : delta;
            } else {
                delta = delta > 0 ? delta - 7 : delta;
            }
            ruleDay += delta;
        }

        long ruleTime = ruleDay * MILLIS_PER_DAY + dateTimeRule.getRuleMillisInDay();
        if (dateTimeRule.getTimeRuleType() != AnnualDateTimeRule.UNIVERSAL_TIME) {
            ruleTime -= prevStdOffset;
        }
        if (dateTimeRule.getTimeRuleType() == AnnualDateTimeRule.WALL_TIME) {
            ruleTime -= prevDstSaving;
        }
        return new Date(ruleTime);
    }
    
    /**
     * Gets the very first time when this rule takes effect.
     * 
     * @param prevStdOffset     The offset from UTC before this rule takes effect
     *                          in milliseconds.
     * @param prevDstSaving     The amount of daylight saving offset from the
     *                          standard time. 
     * 
     * @return  The very first time when this rule takes effect.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public Date getFirstStart(int prevStdOffset, int prevDstSaving) {
        return getStartInYear(startYear, prevStdOffset, prevDstSaving);
    }

    /**
     * Gets the final time when this rule takes effect.
     * 
     * @param prevStdOffset     The offset from UTC before this rule takes effect
     *                          in milliseconds.
     * @param prevDstSaving     The amount of daylight saving offset from the
     *                          standard time.
     * 
     * @return  The very last time when this rule takes effect,
     *          or null if this rule is applied for future dates infinitely.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public Date getFinalStart(int prevStdOffset, int prevDstSaving) {
        if (endYear == MAX_YEAR) {
            return null;
        }
        return getStartInYear(endYear, prevStdOffset, prevDstSaving);
    }

    /**
     * Gets the first time when this rule takes effect after the specified time.
     * 
     * @param base              The first time after this time is returned.
     * @param prevStdOffset     The offset from UTC before this rule takes effect
     *                          in milliseconds.
     * @param prevDstSaving     The amount of daylight saving offset from the
     *                          standard time.
     * @param inclusive         Whether the base time is inclusive or not.
     * 
     * @return  The first time when this rule takes effect after the specified time,
     *          or null when this rule never takes effect after the specified time.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public Date getNextStart(long base, int prevStdOffset, int prevDstSaving, boolean inclusive) {
        int[] fields = Grego.dayToFields(base/MILLIS_PER_DAY, null);
        int year = fields[0];
        if (year < startYear) {
            return getFirstStart(prevStdOffset, prevDstSaving);
        }
        Date d = getStartInYear(year, prevStdOffset, prevDstSaving);
        if (d != null && (d.getTime() < base || (!inclusive && (d.getTime() == base)))) {
            d = getStartInYear(year + 1, prevStdOffset, prevDstSaving);
        }
        return d;
    }

    /**
     * Gets the last time when this rule takes effect before the specified time.
     * 
     * @param base              The last time before this time is returned.
     * @param prevStdOffset     The offset from UTC before this rule takes effect
     *                          in milliseconds.
     * @param prevDstSaving     The amount of daylight saving offset from the
     *                          standard time.
     * @param inclusive         Whether the base time is inclusive or not.
     * 
     * @return  The first time when this rule takes effect after the specified time,
     *          or null when this rule never takes effect after the specified time.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public Date getLastStart(long base, int prevStdOffset, int prevDstSaving, boolean inclusive) {
        int[] fields = Grego.dayToFields(base/MILLIS_PER_DAY, null);
        int year = fields[0];
        if (year > endYear) {
            return getFinalStart(prevStdOffset, prevDstSaving);
        }
        Date d = getStartInYear(year, prevStdOffset, prevDstSaving);
        if (d != null && (d.getTime() > base || (!inclusive && (d.getTime() == base)))) {
            d = getStartInYear(year - 1, prevStdOffset, prevDstSaving);
        }
        return d;
    }
}
