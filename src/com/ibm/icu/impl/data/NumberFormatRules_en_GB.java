/*
 * (C) IBM Corp. 1997-1998.  All Rights Reserved.
 *
 * The program is provided "as is" without any warranty express or
 * implied, including the warranty of non-infringement and the implied
 * warranties of merchantibility and fitness for a particular purpose.
 * IBM will not be liable for any damages suffered by you as a result
 * of using the Program. In no event will IBM be liable for any
 * special, indirect or consequential damages or lost profits even if
 * IBM has been advised of the possibility of their occurrence. IBM
 * will not be liable for any third party claims against you.
 */
package com.ibm.text.resources;

import java.util.ListResourceBundle;

/**
 * RuleBasedNumberFormat data for U.K. English.
 *
 * @author Richard Gillam
 * @version $Version$ $Date: 2000/02/10 06:25:52 $
 */
public class NumberFormatRules_en_GB extends ListResourceBundle {
    /**
     * Puts a copyright in the .class file
     */
    private static final String copyrightNotice
        = "Copyright \u00a91997-1998 IBM Corp.  All rights reserved.";

    public Object[][] getContents() {
        return contents;
    }

    Object[][] contents = {
        /**
         * Spellout rules for U.K. English.  U.K. English has one significant
         * difference from U.S. English: the names for values of 1,000,000,000
         * and higher.  In American English, each successive "-illion" is 1,000
         * times greater than the preceding one: 1,000,000,000 is "one billion"
         * and 1,000,000,000,000 is "one trillion."  In British English, each
         * successive "-illion" is one million times greater than the one before:
         * "one billion" is 1,000,000,000,000 (or what Americans would call a
         * "trillion"), and "one trillion" is 1,000,000,000,000,000,000.
         * 1,000,000,000 in British English is "one thousand million."  (This
         * value is sometimes called a "milliard," but this word seems to have
         * fallen into disuse.)
         */
        { "SpelloutRules",
            "%simplified:\n"
            + "    -x: minus >>;\n"
            + "    x.x: << point >>;\n"
            + "    zero; one; two; three; four; five; six; seven; eight; nine;\n"
            + "    ten; eleven; twelve; thirteen; fourteen; fifteen; sixteen;\n"
            + "        seventeen; eighteen; nineteen;\n"
            + "    20: twenty[->>];\n"
            + "    30: thirty[->>];\n"
            + "    40: forty[->>];\n"
            + "    50: fifty[->>];\n"
            + "    60: sixty[->>];\n"
            + "    70: seventy[->>];\n"
            + "    80: eighty[->>];\n"
            + "    90: ninety[->>];\n"
            + "    100: << hundred[ >>];\n"
            + "    1000: << thousand[ >>];\n"
            + "    1,000,000: << million[ >>];\n"
            + "    1,000,000,000,000: << billion[ >>];\n"
            + "    1,000,000,000,000,000: =#,##0=;\n"
            + "%default:\n"
            + "    -x: minus >>;\n"
            + "    x.x: << point >>;\n"
            + "    =%simplified=;\n"
            + "    100: << hundred[ >%%and>];\n"
            + "    1000: << thousand[ >%%and>];\n"
            + "    100,000>>: << thousand[>%%commas>];\n"
            + "    1,000,000: << million[>%%commas>];\n"
            + "    1,000,000,000,000: << billion[>%%commas>];\n"
            + "    1,000,000,000,000,000: =#,##0=;\n"
            + "%%and:\n"
            + "    and =%default=;\n"
            + "    100: =%default=;\n"
            + "%%commas:\n"
            + "    ' and =%default=;\n"
            + "    100: , =%default=;\n"
            + "    1000: , <%default< thousand, >%default>;\n"
            + "    1,000,000: , =%default=;"
            + "%%lenient-parse:\n"
            + "    & ' ' , ',' ;\n" }
        // Could someone please correct me if I'm wrong about "milliard" falling
        // into disuse, or have missed any other details of how large numbers
        // are rendered.  Also, could someone please provide me with information
        // on which other English-speaking countries use which system?  Right now,
        // I'm assuming that the U.S. system is used in Canada and that all the
        // other English-speaking countries follow the British system.  Can
        // someone out there confirm this?
    };
}
