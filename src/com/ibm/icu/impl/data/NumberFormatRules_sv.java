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
 * RuleBasedNumberFormat data for Swedish
 *
 * @author Richard Gillam
 * @version $Version$ $Date: 2000/02/10 06:25:53 $
 */
public class NumberFormatRules_sv extends ListResourceBundle {
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
         * Spellout rules for Swedish.
         */
        { "SpelloutRules",
            "noll; ett; tv\u00e5; tre; fyra; fem; sex; sjo; \u00e5tta; nio;\n"
            + "tio; elva; tolv; tretton; fjorton; femton; sexton; sjutton; arton; nitton;\n"
            + "20: tjugo[>>];\n"
            + "30: trettio[>>];\n"
            + "40: fyrtio[>>];\n"
            + "50: femtio[>>];\n"
            + "60: sextio[>>];\n"
            + "70: sjuttio[>>];\n"
            + "80: \u00e5ttio[>>];\n"
            + "90: nittio[>>];\n"
            + "100: hundra[>>];\n"
            + "200: <<hundra[>>];\n"
            + "1000: tusen[ >>];\n"
            + "2000: << tusen[ >>];\n"
            + "1,000,000: en miljon[ >>];\n"
            + "2,000,000: << miljon[ >>];\n"
            + "1,000,000,000: en miljard[ >>];\n"
            + "2,000,000,000: << miljard[ >>];\n"
            + "1,000,000,000,000: en biljon[ >>];\n"
            + "2,000,000,000,000: << biljon[ >>];\n"
            + "1,000,000,000,000,000: =#,##0=" }
        // can someone supply me with information on negatives and decimals?
    };
}
