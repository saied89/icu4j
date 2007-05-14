package com.ibm.icu.util;
/**
 * TimeZoneTransition is a class representing a time zone transition.
 * An instance has a time of transition and rules for both before and
 * after the transition.
 * 
 * @draft ICU 3.8
 * @provisional This API might change or be removed in a future release.
 */
public class TimeZoneTransition {
    private final TimeZoneRule from;
    private final TimeZoneRule to;
    private final long time;

    /**
     * Constructs a TimeZoneTransition with the time and the rules before/after
     * the transition.
     * 
     * @param time  The time of transition in milliseconds since the base time.
     * @param from  The time zone rule used before the transition.
     * @param to    The time zone rule used after the transition.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public TimeZoneTransition(long time, TimeZoneRule from, TimeZoneRule to) {
        this.time = time;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the time of transition in milliseconds since the base time.
     * 
     * @return The time of the transition in milliseconds since the base time.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the rule used after the transition.
     * 
     * @return The time zone rule used after the transition.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public TimeZoneRule getTo() {
        return to;
    }

    /**
     * Returns the rule used befre the transition.
     * 
     * @return The time zone rule used after the transition.
     * 
     * @draft ICU 3.8
     * @provisional This API might change or be removed in a future release.
     */
    public TimeZoneRule getFrom() {
        return from;
    }
}
