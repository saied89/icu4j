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
 * RuleBasedNumberFormat data for Russian
 *
 * @author Richard Gillam
 * @version $Version$ $Date: 2000/02/10 06:25:53 $
 */
public class NumberFormatRules_ru extends ListResourceBundle {
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
         * Spellout rules for Russian.
         */
        { "SpelloutRules",
            "\u043d\u043e\u043b\u044c; \u043e\u0434\u0438\u043d; \u0434\u0432\u0430; \u0442\u0440\u0438; "
            + "\u0447\u0435\u0442\u044b\u0440\u0435; \u043f\u044f\u0442; \u0448\u0435\u0441\u0442; "
            + "\u0441\u0435\u043c\u044c; \u0432\u043e\u0441\u0435\u043c\u044c; \u0434\u0435\u0432\u044f\u0442;\n"
            + "10: \u0434\u0435\u0441\u044f\u0442; "
            + "\u043e\u0434\u0438\u043d\u043d\u0430\u0434\u0446\u0430\u0442\u044c;\n"
            + "\u0434\u0432\u0435\u043d\u043d\u0430\u0434\u0446\u0430\u0442\u044c; "
            + "\u0442\u0440\u0438\u043d\u0430\u0434\u0446\u0430\u0442\u044c; "
            + "\u0447\u0435\u0442\u044b\u0440\u043d\u0430\u0434\u0446\u0430\u0442\u044c;\n"
            + "15: \u043f\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c; "
            + "\u0448\u0435\u0441\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c; "
            + "\u0441\u0435\u043c\u043d\u0430\u0434\u0446\u0430\u0442\u044c; "
            + "\u0432\u043e\u0441\u0435\u043c\u043d\u0430\u0434\u0446\u0430\u0442\u044c; "
            + "\u0434\u0435\u0432\u044f\u0442\u043d\u0430\u0434\u0446\u0430\u0442\u044c;\n"
            + "20: \u0434\u0432\u0430\u0434\u0446\u0430\u0442\u044c[ >>];\n"
            + "30: \u0442\u0440\u043b\u0434\u0446\u0430\u0442\u044c[ >>];\n"
            + "40: \u0441\u043e\u0440\u043e\u043a[ >>];\n"
            + "50: \u043f\u044f\u0442\u044c\u0434\u0435\u0441\u044f\u0442[ >>];\n"
            + "60: \u0448\u0435\u0441\u0442\u044c\u0434\u0435\u0441\u044f\u0442[ >>];\n"
            + "70: \u0441\u0435\u043c\u044c\u0434\u0435\u0441\u044f\u0442[ >>];\n"
            + "80: \u0432\u043e\u0441\u0435\u043c\u044c\u0434\u0435\u0441\u044f\u0442[ >>];\n"
            + "90: \u0434\u0435\u0432\u044f\u043d\u043e\u0441\u0442\u043e[ >>];\n"
            + "100: \u0441\u0442\u043e[ >>];\n"
            + "200: << \u0441\u0442\u043e[ >>];\n"
            + "1000: \u0442\u044b\u0441\u044f\u0447\u0430[ >>];\n"
            + "2000: << \u0442\u044b\u0441\u044f\u0447\u0430[ >>];\n"
            + "1,000,000: \u043c\u0438\u043b\u043b\u0438\u043e\u043d[ >>];\n"
            + "2,000,000: << \u043c\u0438\u043b\u043b\u0438\u043e\u043d[ >>];\n"
            + "1,000,000,000: =#,##0=;" }
        // Can someone supply me with information on negatives and decimals?
        // How about words for billions and trillions?
    };
}
