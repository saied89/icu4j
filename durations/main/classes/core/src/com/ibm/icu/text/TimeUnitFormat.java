/*
 **************************************************************************
 * Copyright (C) 2008-2013, Google, International Business Machines
 * Corporation and others. All Rights Reserved.
 **************************************************************************
 */
package com.ibm.icu.text;

import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.util.TimePeriod;
import com.ibm.icu.util.TimeUnit;
import com.ibm.icu.util.TimeUnitAmount;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import com.ibm.icu.util.UResourceBundle;


/**
 * Format or parse a TimeUnitAmount, using plural rules for the units where available.
 *
 * <P>
 * Code Sample: 
 * <pre>
 *   // create a time unit instance.
 *   // only SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, and YEAR are supported
 *   TimeUnit timeUnit = TimeUnit.SECOND;
 *   // create time unit amount instance - a combination of Number and time unit
 *   TimeUnitAmount source = new TimeUnitAmount(2, timeUnit);
 *   // create time unit format instance
 *   TimeUnitFormat format = new TimeUnitFormat();
 *   // set the locale of time unit format
 *   format.setLocale(new ULocale("en"));
 *   // format a time unit amount
 *   String formatted = format.format(source);
 *   System.out.println(formatted);
 *   try {
 *       // parse a string into time unit amount
 *       TimeUnitAmount result = (TimeUnitAmount) format.parseObject(formatted);
 *       // result should equal to source 
 *   } catch (ParseException e) {
 *   }
 * </pre>
 *
 * <P>
 * @see TimeUnitAmount
 * @see TimeUnitFormat
 * @author markdavis
 * @stable ICU 4.0
 */
public class TimeUnitFormat extends MeasureFormat {

    /**
     * Constant for full name style format. 
     * For example, the full name for "hour" in English is "hour" or "hours".
     * @stable ICU 4.2
     */
    public static final int FULL_NAME = 0;
    /**
     * Constant for abbreviated name style format. 
     * For example, the abbreviated name for "hour" in English is "hr" or "hrs".
     * @stable ICU 4.2
     */
    public static final int ABBREVIATED_NAME = 1;
    
    /**
     * Constant for numeric style format. 
     * NUMERIC strives to be as brief as possible. For example: 3:05:47.
     * @draft ICU 52
     */
    public static final int NUMERIC = 2;

    private static final int TOTAL_STYLES = 3;

    private static final long serialVersionUID = -3707773153184971529L;
  
    private static final String DEFAULT_PATTERN_FOR_SECOND = "{0} s";
    private static final String DEFAULT_PATTERN_FOR_MINUTE = "{0} min";
    private static final String DEFAULT_PATTERN_FOR_HOUR = "{0} h";
    private static final String DEFAULT_PATTERN_FOR_DAY = "{0} d";
    private static final String DEFAULT_PATTERN_FOR_WEEK = "{0} w";
    private static final String DEFAULT_PATTERN_FOR_MONTH = "{0} m";
    private static final String DEFAULT_PATTERN_FOR_YEAR = "{0} y";

    private NumberFormat format;
    private ULocale locale;
    private transient Map<TimeUnit, Map<String, Object[]>> timeUnitToCountToPatterns;
    private transient PluralRules pluralRules;
    private transient ListFormatter listFormatter;
    private transient DateFormat hourMinute;
    private transient DateFormat minuteSecond;
    private transient DateFormat hourMinuteSecond;
    private transient boolean isReady;
    private int style;

    /**
     * Create empty format using full name style, for example, "hours". 
     * Use setLocale and/or setFormat to modify.
     * @stable ICU 4.0
     */
    public TimeUnitFormat() {
        isReady = false;
        style = FULL_NAME;

    }

    /**
     * Create TimeUnitFormat given a ULocale, and using full name style.
     * @param locale   locale of this time unit formatter.
     * @stable ICU 4.0
     */
    public TimeUnitFormat(ULocale locale) {
        this(locale, FULL_NAME);
    }

    /**
     * Create TimeUnitFormat given a Locale, and using full name style.
     * @param locale   locale of this time unit formatter.
     * @stable ICU 4.0
     */
    public TimeUnitFormat(Locale locale) {
        this(locale, FULL_NAME);
    }

    /**
     * Create TimeUnitFormat given a ULocale and a formatting style.
     * @param locale   locale of this time unit formatter.
     * @param style    format style, either FULL_NAME or ABBREVIATED_NAME style.
     * @throws IllegalArgumentException if the style is not FULL_NAME or
     *                                  ABBREVIATED_NAME style.
     * @stable ICU 4.2
     */
    public TimeUnitFormat(ULocale locale, int style) {
        if (style < FULL_NAME || style >= TOTAL_STYLES) {
            throw new IllegalArgumentException("style should be either FULL_NAME or ABBREVIATED_NAME style");
        }
        this.style = style;
        this.locale = locale;
        isReady = false;
    }

    /**
     * Create TimeUnitFormat given a Locale and a formatting style.
     * @stable ICU 4.2
     */
    public TimeUnitFormat(Locale locale, int style) {
        this(ULocale.forLocale(locale),  style);
    }

    /**
     * Set the locale used for formatting or parsing.
     * @param locale   locale of this time unit formatter.
     * @return this, for chaining.
     * @stable ICU 4.0
     */
    public TimeUnitFormat setLocale(ULocale locale) {
        if ( locale != this.locale ) {
            this.locale = locale;
            isReady = false;
        }
        return this;
    }
    
    /**
     * Set the locale used for formatting or parsing.
     * @param locale   locale of this time unit formatter.
     * @return this, for chaining.
     * @stable ICU 4.0
     */
    public TimeUnitFormat setLocale(Locale locale) {
        return setLocale(ULocale.forLocale(locale));
    }
    
    /**
     * Set the format used for formatting or parsing. If null or not available, use the getNumberInstance(locale).
     * @param format   the number formatter.
     * @return this, for chaining.
     * @stable ICU 4.0
     */
    public TimeUnitFormat setNumberFormat(NumberFormat format) {
        if (format == this.format) {
            return this;
        }
        if ( format == null ) {
            if ( locale == null ) {
                isReady = false;
                return this;
            } else {
                this.format = NumberFormat.getNumberInstance(locale);
            }
        } else {
            this.format = format;
        }
        // reset the number formatter in the timeUnitToCountToPatterns map
        if (isReady == false) {
            return this;
        }
        for (Map<String, Object[]> countToPattern : timeUnitToCountToPatterns.values()) {
            for (Object[] pair : countToPattern.values()) {
                MessageFormat pattern = (MessageFormat)pair[FULL_NAME];
                pattern.setFormatByArgumentIndex(0, format);
                pattern = (MessageFormat)pair[ABBREVIATED_NAME];
                pattern.setFormatByArgumentIndex(0, format);
            }
        }
        return this;
    }


    /**
     * Format a TimeUnitAmount.
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     * @stable ICU 4.0
     */
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
            FieldPosition pos) {
        if ( !(obj instanceof TimeUnitAmount) ) {
            throw new IllegalArgumentException("can not format non TimeUnitAmount object");
        }
        if (!isReady) {
            setup();
        }
        TimeUnitAmount amount = (TimeUnitAmount) obj;
        Map<String, Object[]> countToPattern = timeUnitToCountToPatterns.get(amount.getTimeUnit());
        double number = amount.getNumber().doubleValue();
        String count = pluralRules.select(number);
        // A hack since NUMERIC really isn't a full fledged style
        int effectiveStyle = (style == NUMERIC) ? ABBREVIATED_NAME : style;
        MessageFormat pattern = (MessageFormat)(countToPattern.get(count))[effectiveStyle];
        return pattern.format(new Object[]{amount.getNumber()}, toAppendTo, pos);
    }
    
    /**
     * Formats a TimePeriod. Currently there is no way to parse a formatted TimePeriod.
     * @param timePeriod the TimePeriod to format.
     * @return the formatted string.
     * @draft ICU 52
     */
    public String formatTimePeriod(TimePeriod timePeriod) {
        if (!isReady) {
            setup();
        }
        if (style == NUMERIC) {
            String result = formatPeriodAsNumeric(timePeriod);
            if (result != null) {
                return result;
            }
        }
        String[] items = new String[timePeriod.size()];
        int idx = 0;
        for (TimeUnitAmount amount : timePeriod) {
            items[idx++] = format(amount);
        }
        return listFormatter.format((Object[]) items);   
    }

    /**
     * Parse a TimeUnitAmount. Parsing TimePeriod objects is not supported.
     * Calling parseObject on a formatted TimePeriod will either
     * fail or return a TimeUnitAmount representing just one of the components of the
     * original TimePeriod.  
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     * @stable ICU 4.0
     */
    public Object parseObject(String source, ParsePosition pos) {
        if (!isReady) {
            setup();
        }
        Number resultNumber = null;
        TimeUnit resultTimeUnit = null;
        int oldPos = pos.getIndex();
        int newPos = -1;
        int longestParseDistance = 0;
        String countOfLongestMatch = null;
        // we don't worry too much about speed on parsing, but this can be optimized later if needed.
        // Parse by iterating through all available patterns
        // and looking for the longest match.
        for (TimeUnit timeUnit : timeUnitToCountToPatterns.keySet()) {
            Map<String, Object[]> countToPattern = timeUnitToCountToPatterns.get(timeUnit);
            for (Entry<String, Object[]> patternEntry : countToPattern.entrySet()) {
              String count = patternEntry.getKey();
              for (int styl = FULL_NAME; styl < TOTAL_STYLES; ++styl) {
                  if (styl == NUMERIC) {
                      // Numeric isn't a real style, so skip it.
                      continue;
                  }
                MessageFormat pattern = (MessageFormat)(patternEntry.getValue())[styl];
                pos.setErrorIndex(-1);
                pos.setIndex(oldPos);
                // see if we can parse
                Object parsed = pattern.parseObject(source, pos);
                if ( pos.getErrorIndex() != -1 || pos.getIndex() == oldPos ) {
                    // nothing parsed
                    continue;
                }
                Number temp = null;
                if ( ((Object[])parsed).length != 0 ) {
                    // pattern with Number as beginning,
                    // such as "{0} d".
                    // check to make sure that the timeUnit is consistent
                    temp = (Number)((Object[])parsed)[0];
                    String select = pluralRules.select(temp.doubleValue());
                    if (!count.equals(select)) {
                        continue;
                    }
                }
                int parseDistance = pos.getIndex() - oldPos;
                if ( parseDistance > longestParseDistance ) {
                    resultNumber = temp;
                    resultTimeUnit = timeUnit;
                    newPos = pos.getIndex();
                    longestParseDistance = parseDistance;
                    countOfLongestMatch = count;
                }
            }
          }
        }
        /* After find the longest match, parse the number.
         * Result number could be null for the pattern without number pattern.
         * such as unit pattern in Arabic.
         * When result number is null, use plural rule to set the number.
         */
        if (resultNumber == null && longestParseDistance != 0) {
            // set the number using plurrual count
            if ( countOfLongestMatch.equals("zero") ) {
                resultNumber = Integer.valueOf(0);
            } else if ( countOfLongestMatch.equals("one") ) {
                resultNumber = Integer.valueOf(1);
            } else if ( countOfLongestMatch.equals("two") ) {
                resultNumber = Integer.valueOf(2);
            } else {
                // should not happen.
                // TODO: how to handle?
                resultNumber = Integer.valueOf(3);
            }
        }
        if (longestParseDistance == 0) {
            pos.setIndex(oldPos);
            pos.setErrorIndex(0);
            return null;
        } else {
            pos.setIndex(newPos);
            pos.setErrorIndex(-1);
            return new TimeUnitAmount(resultNumber, resultTimeUnit);
        }
    }
    
    
    /*
     * Initialize locale, number formatter, plural rules, and
     * time units patterns.
     * Initially, we are storing all of these as MessageFormats.
     * I think it might actually be simpler to make them Decimal Formats later.
     */
    private void setup() {
        if (locale == null) {
            if (format != null) {
                locale = format.getLocale(null);
            } else {
                locale = ULocale.getDefault(Category.FORMAT);
            }
        }
        if (format == null) {
            format = NumberFormat.getNumberInstance(locale);
        }
        pluralRules = PluralRules.forLocale(locale);
        if (style == FULL_NAME) {
          listFormatter = ListFormatter.getInstance(locale, ListFormatter.Style.DURATION);
        } else {
          listFormatter = ListFormatter.getInstance(locale, ListFormatter.Style.DURATION_SHORT);
        }
        hourMinute = loadNumericDurationFormat(locale, "hm");
        minuteSecond = loadNumericDurationFormat(locale, "ms");
        hourMinuteSecond = loadNumericDurationFormat(locale, "hms");
        timeUnitToCountToPatterns = new HashMap<TimeUnit, Map<String, Object[]>>();
        Set<String> pluralKeywords = pluralRules.getKeywords();
        setup("units", timeUnitToCountToPatterns, FULL_NAME, pluralKeywords);
        setup("unitsShort", timeUnitToCountToPatterns, ABBREVIATED_NAME, pluralKeywords);
        isReady = true;
    }
    
    // type is one of "hm", "ms" or "hms"
    private static DateFormat loadNumericDurationFormat(ULocale ulocale, String type) {
        ICUResourceBundle r = (ICUResourceBundle)UResourceBundle.
                getBundleInstance(ICUResourceBundle.ICU_BASE_NAME, ulocale);
        r = r.getWithFallback(String.format("durationUnits/%s", type));
        // We replace 'h' with 'H' because 'h' does not make sense in the context of durations.
        DateFormat result = new SimpleDateFormat(r.getString().replace("h", "H"));
        result.setTimeZone(TimeZone.GMT_ZONE);
        return result;
    }
    
    private String formatPeriodAsNumeric(TimePeriod timePeriod) {
        TimeUnit biggestUnit = null, smallestUnit = null;
        Number smallestUnitAmount = null;
        for (TimeUnitAmount tua : timePeriod) {
            if (biggestUnit == null) {
                biggestUnit = tua.getTimeUnit();
            }
            smallestUnit = tua.getTimeUnit();
            smallestUnitAmount = tua.getNumber();
        }
        long millis = (long) (((getAmountOrZero(timePeriod, TimeUnit.HOUR) * 60.0
                + getAmountOrZero(timePeriod, TimeUnit.MINUTE)) * 60.0
                + getAmountOrZero(timePeriod, TimeUnit.SECOND)) * 1000.0 + 0.5);
        Date d = new Date(millis);
        // We have to trim the result of  MessageFormat.format() not sure why.
        if (biggestUnit == TimeUnit.HOUR && smallestUnit == TimeUnit.SECOND) {
            return numericFormat(
                    d, hourMinuteSecond, DateFormat.Field.SECOND, smallestUnitAmount);
        }
        if (biggestUnit == TimeUnit.MINUTE && smallestUnit == TimeUnit.SECOND) {
            return numericFormat(
                    d, minuteSecond, DateFormat.Field.SECOND, smallestUnitAmount);          
        }
        if (biggestUnit == TimeUnit.HOUR && smallestUnit == TimeUnit.MINUTE) {
            return numericFormat(d, hourMinute, DateFormat.Field.MINUTE, smallestUnitAmount);
        }
        return null;
    }

    /**
     * numericFormat allows us to show fractional durations using numeric
     * style e.g 12:34:56.7. This function is necessary because there is no way to express
     * fractions of durations other than seconds with current DateFormat objects.
     * 
     * After formatting the duration using a DateFormat object in the usual way, it
     * replaces the smallest field in the formatted string with the exact fractional
     * amount of that smallest field formatted with this object's NumberFormat object.
     * 
     * @param duration The duration to format in milliseconds. The loss of precision here
     * is ok because we also pass in the exact amount of the smallest field.
     * @param formatter formats the date.
     * @param smallestField the smallest defined field in duration to be formatted.
     * @param smallestAmount the exact fractional value of the smallest amount. 
     * @return duration formatted numeric style.
     */
    private String numericFormat(
            Date duration,
            DateFormat formatter,
            DateFormat.Field smallestField,
            Number smallestAmount) {
        // Format the smallest amount ahead of time.
        String smallestAmountFormatted = format.format(smallestAmount);
        
        // Format the duration using the provided DateFormat object. The smallest
        // field in this result will be missing the fractional part.
        AttributedCharacterIterator iterator = formatter.formatToCharacterIterator(duration);
        
        // The final formatted duration will be written here.
        StringBuilder builder = new StringBuilder();
        
        // iterate through formatted text copying to 'builder' one character at a time.
        // When we get to the smallest amount, skip over it and copy
        // 'smallestAmountFormatted' to the builder instead.
        for (iterator.first(); iterator.getIndex() < iterator.getEndIndex();) {
            if (iterator.getAttributes().containsKey(smallestField)) {
                builder.append(smallestAmountFormatted);
                iterator.setIndex(iterator.getRunLimit(smallestField));
            } else {
                builder.append(iterator.current());
                iterator.next();
            }
        }
        return builder.toString();
    }

    private double getAmountOrZero(TimePeriod timePeriod, TimeUnit timeUnit) {
        TimeUnitAmount tua = timePeriod.getAmount(timeUnit);
        if (tua == null) {
            return 0.0;
        }
        return tua.getNumber().doubleValue();
    }

    private void setup(String resourceKey, Map<TimeUnit, Map<String, Object[]>> timeUnitToCountToPatterns,
                       int style, Set<String> pluralKeywords) {
        // fill timeUnitToCountToPatterns from resource file
        try {
            ICUResourceBundle resource = (ICUResourceBundle)UResourceBundle.getBundleInstance(ICUResourceBundle.ICU_BASE_NAME, locale);
            ICUResourceBundle unitsRes = resource.getWithFallback(resourceKey);
            int size = unitsRes.getSize();
            for ( int index = 0; index < size; ++index) {
                String timeUnitName = unitsRes.get(index).getKey();
                TimeUnit timeUnit = null;
                if ( timeUnitName.equals("year") ) {
                    timeUnit = TimeUnit.YEAR;
                } else if ( timeUnitName.equals("month") ) {
                    timeUnit = TimeUnit.MONTH;
                } else if ( timeUnitName.equals("day") ) {
                    timeUnit = TimeUnit.DAY;
                } else if ( timeUnitName.equals("hour") ) {
                    timeUnit = TimeUnit.HOUR;
                } else if ( timeUnitName.equals("minute") ) {
                    timeUnit = TimeUnit.MINUTE;
                } else if ( timeUnitName.equals("second") ) {
                    timeUnit = TimeUnit.SECOND;
                } else if ( timeUnitName.equals("week") ) {
                    timeUnit = TimeUnit.WEEK;
                } else {
                    continue;
                }
                ICUResourceBundle oneUnitRes = unitsRes.getWithFallback(timeUnitName);
                int count = oneUnitRes.getSize();
                Map<String, Object[]> countToPatterns = timeUnitToCountToPatterns.get(timeUnit);
                if (countToPatterns ==  null) {
                    countToPatterns = new TreeMap<String, Object[]>();
                    timeUnitToCountToPatterns.put(timeUnit, countToPatterns);
                } 
                for ( int pluralIndex = 0; pluralIndex < count; ++pluralIndex) {
                    String pluralCount = oneUnitRes.get(pluralIndex).getKey();
                    if (!pluralKeywords.contains(pluralCount))
                        continue;
                    String pattern = oneUnitRes.get(pluralIndex).getString();
                    final MessageFormat messageFormat = new MessageFormat(pattern, locale);
                    if (format != null) {
                        messageFormat.setFormatByArgumentIndex(0, format);
                    }
                    // save both full name and abbreviated name in one table
                    // is good space-wise, but it degrades performance, 
                    // since it needs to check whether the needed space 
                    // is already allocated or not.
                    Object[] pair = countToPatterns.get(pluralCount);
                    if (pair == null) {
                        pair = new Object[2];
                        countToPatterns.put(pluralCount, pair);
                    } 
                    pair[style] = messageFormat;
                }
            }
        } catch ( MissingResourceException e ) {
        }

        // there should be patterns for each plural rule in each time unit.
        // For each time unit, 
        //     for each plural rule, following is unit pattern fall-back rule:
        //         ( for example: "one" hour )
        //         look for its unit pattern in its locale tree.
        //         if pattern is not found in its own locale, such as de_DE,
        //         look for the pattern in its parent, such as de,
        //         keep looking till found or till root.
        //         if the pattern is not found in root either,
        //         fallback to plural count "other",
        //         look for the pattern of "other" in the locale tree:
        //         "de_DE" to "de" to "root".
        //         If not found, fall back to value of 
        //         static variable DEFAULT_PATTERN_FOR_xxx, such as "{0} h". 
        //
        // Following is consistency check to create pattern for each
        // plural rule in each time unit using above fall-back rule.
        //
        final TimeUnit[] timeUnits = TimeUnit.values();
        Set<String> keywords = pluralRules.getKeywords();
        for ( int i = 0; i < timeUnits.length; ++i ) {
            // for each time unit, 
            // get all the patterns for each plural rule in this locale.
            final TimeUnit timeUnit = timeUnits[i];
            Map<String, Object[]> countToPatterns = timeUnitToCountToPatterns.get(timeUnit);
            if (countToPatterns == null) {
                countToPatterns = new TreeMap<String, Object[]>();
                timeUnitToCountToPatterns.put(timeUnit, countToPatterns);
            }
            for (String pluralCount : keywords) {
                if ( countToPatterns.get(pluralCount) == null ||
                     countToPatterns.get(pluralCount)[style] == null ) {
                    // look through parents
                    searchInTree(resourceKey, style, timeUnit, pluralCount, pluralCount, countToPatterns);
                }
            }
        }
    }



    // srcPluralCount is the original plural count on which the pattern is
    // searched for.
    // searchPluralCount is the fallback plural count.
    // For example, to search for pattern for ""one" hour",
    // "one" is the srcPluralCount,
    // if the pattern is not found even in root, fallback to 
    // using patterns of plural count "other", 
    // then, "other" is the searchPluralCount.
    private void searchInTree(String resourceKey, int styl,
                              TimeUnit timeUnit, String srcPluralCount,
                              String searchPluralCount, Map<String, Object[]> countToPatterns) {
        ULocale parentLocale=locale;
        String srcTimeUnitName = timeUnit.toString();
        while ( parentLocale != null ) {
            try {
                // look for pattern for srcPluralCount in locale tree
                ICUResourceBundle unitsRes = (ICUResourceBundle) UResourceBundle.getBundleInstance(ICUResourceBundle.ICU_BASE_NAME, parentLocale);
                unitsRes = unitsRes.getWithFallback(resourceKey);
                ICUResourceBundle oneUnitRes = unitsRes.getWithFallback(srcTimeUnitName);
                String pattern = oneUnitRes.getStringWithFallback(searchPluralCount);
                final MessageFormat messageFormat = new MessageFormat(pattern, locale);
                if (format != null) {
                    messageFormat.setFormatByArgumentIndex(0, format);
                }
                Object[] pair = countToPatterns.get(srcPluralCount);
                if (pair == null) {
                    pair = new Object[2];
                    countToPatterns.put(srcPluralCount, pair);
                }
                pair[styl] = messageFormat;
                return;
            } catch ( MissingResourceException e ) {
            }
            parentLocale=parentLocale.getFallback();
        }

        // if no unitsShort resource was found even after fallback to root locale
        // then search the units resource fallback from the current level to root
        if ( parentLocale == null && resourceKey.equals("unitsShort") ) {
            searchInTree("units", styl, timeUnit, srcPluralCount, searchPluralCount, countToPatterns);
            if ( countToPatterns != null &&
                    countToPatterns.get(srcPluralCount) != null &&
                    countToPatterns.get(srcPluralCount)[styl] != null ) {
                return;
            }
        }

        // if not found the pattern for this plural count at all,
        // fall-back to plural count "other"
        if ( searchPluralCount.equals("other") ) {
            // set default fall back the same as the resource in root
            MessageFormat messageFormat = null;
            if ( timeUnit == TimeUnit.SECOND ) {
                messageFormat = new MessageFormat(DEFAULT_PATTERN_FOR_SECOND, locale);
            } else if ( timeUnit == TimeUnit.MINUTE ) {
                messageFormat = new MessageFormat(DEFAULT_PATTERN_FOR_MINUTE, locale);
            } else if ( timeUnit == TimeUnit.HOUR ) {
                messageFormat = new MessageFormat(DEFAULT_PATTERN_FOR_HOUR, locale);
            } else if ( timeUnit == TimeUnit.WEEK ) {
                messageFormat = new MessageFormat(DEFAULT_PATTERN_FOR_WEEK, locale);
            } else if ( timeUnit == TimeUnit.DAY ) {
                messageFormat = new MessageFormat(DEFAULT_PATTERN_FOR_DAY, locale);
            } else if ( timeUnit == TimeUnit.MONTH ) {
                messageFormat = new MessageFormat(DEFAULT_PATTERN_FOR_MONTH, locale);
            } else if ( timeUnit == TimeUnit.YEAR ) {
                messageFormat = new MessageFormat(DEFAULT_PATTERN_FOR_YEAR, locale);
            }
            if (format != null && messageFormat != null) {
                messageFormat.setFormatByArgumentIndex(0, format);
            }
            Object[] pair = countToPatterns.get(srcPluralCount);
            if (pair == null) {
                pair = new Object[2];
                countToPatterns.put(srcPluralCount, pair);
            }
            pair[styl] = messageFormat;
        } else {
            // fall back to rule "other", and search in parents
            searchInTree(resourceKey, styl, timeUnit, srcPluralCount, "other", countToPatterns);
        }
    }
}
