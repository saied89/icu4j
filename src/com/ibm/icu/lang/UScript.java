/**
*******************************************************************************
* Copyright (C) 2001, International Business Machines Corporation and         *
* others. All Rights Reserved.                                                *
*******************************************************************************
*/

package com.ibm.icu.lang;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.LocaleUtility;
import com.ibm.icu.impl.UCharacterProperty;

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * A class to reflect UTR #24: Script Names
 * (based on ISO 15924:2000, "Code for the representation of names of
 * scripts").  UTR #24 describes the basis for a new Unicode data file,
 * Scripts.txt.
 */
public final class UScript {
    /**
     * Puts a copyright in the .class file
     */
    private static final String copyrightNotice
        = "Copyright \u00a92001 IBM Corp.  All rights reserved.";
        
    public static final int INVALID_CODE = -1;
    public static final int COMMON       =  0;  /* Zyyy */
    public static final int INHERITED    =  1;  /* Qaai */
    public static final int ARABIC       =  2;  /* Arab */
    public static final int ARMENIAN     =  3;  /* Armn */
    public static final int BENGALI      =  4;  /* Beng */
    public static final int BOPOMOFO     =  5;  /* Bopo */
    public static final int CHEROKEE     =  6;  /* Cher */
    public static final int COPTIC       =  7;  /* Qaac */
    public static final int CYRILLIC     =  8;  /* Cyrl (Cyrs) */
    public static final int DESERET      =  9;  /* Dsrt */
    public static final int DEVANAGARI   = 10;  /* Deva */
    public static final int ETHIOPIC     = 11;  /* Ethi */
    public static final int GEORGIAN     = 12;  /* Geor (Geon; Geoa) */
    public static final int GOTHIC       = 13;  /* Goth */
    public static final int GREEK        = 14;  /* Grek */
    public static final int GUJARATI     = 15;  /* Gujr */
    public static final int GURMUKHI     = 16;  /* Guru */
    public static final int HAN          = 17;  /* Hani */
    public static final int HANGUL       = 18;  /* Hang */
    public static final int HEBREW       = 19;  /* Hebr */
    public static final int HIRAGANA     = 20;  /* Hira */
    public static final int KANNADA      = 21;  /* Knda */
    public static final int KATAKANA     = 22;  /* Kana */
    public static final int KHMER        = 23;  /* Khmr */
    public static final int LAO          = 24;  /* Laoo */
    public static final int LATIN        = 25;  /* Latn (Latf; Latg) */
    public static final int MALAYALAM    = 26;  /* Mlym */
    public static final int MONGOLIAN    = 27;  /* Mong */
    public static final int MYANMAR      = 28;  /* Mymr */
    public static final int OGHAM        = 29;  /* Ogam */
    public static final int OLD_ITALIC   = 30;  /* Ital */
    public static final int ORIYA        = 31;  /* Orya */
    public static final int RUNIC        = 32;  /* Runr */
    public static final int SINHALA      = 33;  /* Sinh */
    public static final int SYRIAC       = 34;  /* Syrc (Syrj; Syrn; Syre) */
    public static final int TAMIL        = 35;  /* Taml */
    public static final int TELUGU       = 36;  /* Telu */
    public static final int THAANA       = 37;  /* Thaa */
    public static final int THAI         = 38;  /* Thai */
    public static final int TIBETAN      = 39;  /* Tibt */
    public static final int UCAS         = 40;  /* Cans */
    public static final int YI           = 41;  /* Yiii */
    public static final int TAGALOG      = 42;  /* Tglg */
    public static final int HANUNOO      = 43;  /* Hano */
    public static final int BUHID        = 44;  /* Buhd */
    public static final int TAGBANWA     = 45;  /* Tagb */
    public static final int CODE_LIMIT   = 46; 
    
    private static final int SCRIPT_MASK   = 0x0000007f;
    private static final UCharacterProperty prop= UCharacterProperty.getInstance();
    
    /**
     * Helper function to find the code from locale.
     * @param Locale the locale.
     * @exception MissingResourceException if LocaleScript cannot be opened
     */
    private static int[] findCodeFromLocale(Locale locale) {

        ResourceBundle rb = ICULocaleData.getLocaleElements(locale);

        // if rb is not a strict fallback of the requested locale, return null
        if (rb==null || !LocaleUtility.isFallbackOf(rb.getLocale(), locale)) {
            return null;
        }

        String[] scripts = rb.getStringArray("LocaleScript");
        int[] result = new int[scripts.length];
        int w = 0;
        for (int i = 0; i < scripts.length; ++i) {
            try {
                int code = UCharacter.getPropertyValueEnum(UProperty.SCRIPT,
                                                           scripts[i]);
                result[w++] = code;
            } catch (IllegalArgumentException e) {}
        }

        if (w < result.length) {
            throw new InternalError("bad locale data, listed " + scripts.length + " scripts but found only " + w);
        }

        return result;
    }
         
    /**
     * Gets a script codes associated with the given locale or ISO 15924 abbreviation or name. 
     * Returns MALAYAM given "Malayam" OR "Mlym".
     * Returns LATIN given "en" OR "en_US" 
     * @param locale Locale
     * @return The script codes array. null if the the code cannot be found. 
     * @exception MissingResourceException
     * @draft 2.1
     */
    public static final int[] getCode(Locale locale)
        throws MissingResourceException {
        return findCodeFromLocale(locale);
    }
    
    /**
     * Gets a script codes associated with the given locale or ISO 15924 abbreviation or name. 
     * Returns MALAYAM given "Malayam" OR "Mlym".
     * Returns LATIN given "en" OR "en_US" 
     *
     * <p>Note: To search by short or long script alias only, use
     * UCharacater.getPropertyValueEnum(UProperty.SCRIPT, alias)
     * instead.  This does a fast lookup with no access of the locale
     * data.
     * @param nameOrAbbrOrLocale name of the script or ISO 15924 code or locale
     * @return The script codes array. null if the the code cannot be found.
     * @draft 2.1
     */
    public static final int[] getCode(String nameOrAbbrOrLocale){
        try {
            return new int[] {
                UCharacter.getPropertyValueEnum(UProperty.SCRIPT,
                                                nameOrAbbrOrLocale)
            };
        } catch (IllegalArgumentException e) {
            return findCodeFromLocale(LocaleUtility.getLocaleFromName(nameOrAbbrOrLocale));
        }
    }

    /** 
     * Gets the script code associated with the given codepoint.
     * Returns UScript.MALAYAM given 0x0D02 
     * @param codepoint UChar32 codepoint
     * @return The script code 
     * @exception IllegalArgumentException
     * @draft 2.1
     */
    public static final int getScript(int codepoint){
        if (codepoint >= UCharacter.MIN_VALUE & codepoint <= UCharacter.MAX_VALUE) {
            return (int)(prop.getAdditional(codepoint,0) & SCRIPT_MASK);
        }else{
            throw new IllegalArgumentException(Integer.toString(codepoint));
        } 
    }
    
    /**
     * Gets a script name associated with the given script code. 
     * Returns  "Malayam" given MALAYAM
     * @param scriptCode int script code
     * @return script name as a string in full as given in TR#24
     * @exception IllegalArgumentException
     * @draft 2.1
     */
    public static final String getName(int scriptCode){
        return UCharacter.getPropertyValueName(UProperty.SCRIPT,
                                               scriptCode,
                                               UProperty.NameChoice.LONG);
    }
    
    /**
     * Gets a script name associated with the given script code. 
     * Returns  "Mlym" given MALAYAM
     * @param scriptCode int script code 
     * @return script abbreviated name as a string  as given in TR#24
     * @exception IllegalArgumentException
     * @draft 2.1
     */
    public static final String getShortName(int scriptCode){
        return UCharacter.getPropertyValueName(UProperty.SCRIPT,
                                               scriptCode,
                                               UProperty.NameChoice.SHORT);
    }
}

