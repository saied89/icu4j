/*
**********************************************************************
* Copyright (c) 2004-2006, International Business Machines
* Corporation and others.  All Rights Reserved.
**********************************************************************
* Author: Alan Liu
* Created: April 6, 2004
* Since: ICU 3.0
**********************************************************************
*/
package com.ibm.icu.dev.test.format;

import java.text.ChoiceFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.UFormat;
import com.ibm.icu.util.ULocale;

public class TestMessageFormat extends com.ibm.icu.dev.test.TestFmwk {

    public static void main(String[] args) throws Exception {
        new TestMessageFormat().run(args);
    }

    public void TestBug3()
    {
        double myNumber = -123456;
        DecimalFormat form = null;
        Locale locale[] = {
            new Locale("ar", "", ""),
            new Locale("be", "", ""),
            new Locale("bg", "", ""),
            new Locale("ca", "", ""),
            new Locale("cs", "", ""),
            new Locale("da", "", ""),
            new Locale("de", "", ""),
            new Locale("de", "AT", ""),
            new Locale("de", "CH", ""),
            new Locale("el", "", ""),       // 10
            new Locale("en", "CA", ""),
            new Locale("en", "GB", ""),
            new Locale("en", "IE", ""),
            new Locale("en", "US", ""),
            new Locale("es", "", ""),
            new Locale("et", "", ""),
            new Locale("fi", "", ""),
            new Locale("fr", "", ""),
            new Locale("fr", "BE", ""),
            new Locale("fr", "CA", ""),     // 20
            new Locale("fr", "CH", ""),
            new Locale("he", "", ""),
            new Locale("hr", "", ""),
            new Locale("hu", "", ""),
            new Locale("is", "", ""),
            new Locale("it", "", ""),
            new Locale("it", "CH", ""),
            new Locale("ja", "", ""),
            new Locale("ko", "", ""),
            new Locale("lt", "", ""),       // 30
            new Locale("lv", "", ""),
            new Locale("mk", "", ""),
            new Locale("nl", "", ""),
            new Locale("nl", "BE", ""),
            new Locale("no", "", ""),
            new Locale("pl", "", ""),
            new Locale("pt", "", ""),
            new Locale("ro", "", ""),
            new Locale("ru", "", ""),
            new Locale("sh", "", ""),       // 40
            new Locale("sk", "", ""),
            new Locale("sl", "", ""),
            new Locale("sq", "", ""),
            new Locale("sr", "", ""),
            new Locale("sv", "", ""),
            new Locale("tr", "", ""),
            new Locale("uk", "", ""),
            new Locale("zh", "", ""),
            new Locale("zh", "TW", "")      // 49
        };
        StringBuffer buffer = new StringBuffer();
        ParsePosition parsePos = new ParsePosition(0);
        int i;
        for (i= 0; i < 49; i++) {
    //        form = (DecimalFormat)NumberFormat.getCurrencyInstance(locale[i]);
            form = (DecimalFormat)NumberFormat.getInstance(locale[i]);
            if (form == null) {
                errln("Number format creation failed for " + locale[i].getDisplayName());
                continue;
            }
            FieldPosition pos = new FieldPosition(0);
            buffer.setLength(0);
            form.format(myNumber, buffer, pos);
            parsePos.setIndex(0);
            Object result = form.parse(buffer.toString(), parsePos);
            logln(locale[i].getDisplayName() + " -> " + result);
            if (parsePos.getIndex() != buffer.length()) {
                errln("Number format parse failed.");
            }
        }
    }

    public void TestBug1()
    {
        final double limit[] = {0.0, 1.0, 2.0};
        final String formats[] = {"0.0<=Arg<1.0",
                                  "1.0<=Arg<2.0",
                                  "2.0<-Arg"};
        ChoiceFormat cf = new ChoiceFormat(limit, formats);
        FieldPosition status = new FieldPosition(0);
        assertEquals("ChoiceFormat.format", formats[1], cf.format(1));
    }

    public void TestBug2()
    {
        String result;
        // {sfb} use double format in pattern, so result will match (not strictly necessary)
        final String pattern = "There {0,choice,0.0#are no files|1.0#is one file|1.0<are {0, number} files} on disk {1}. ";
        logln("The input pattern : " + pattern);
        try {
            MessageFormat fmt = new MessageFormat(pattern);
            assertEquals("toPattern", pattern, fmt.toPattern());
        } catch (IllegalArgumentException e) {
            errln("MessageFormat pattern creation failed.");
        }
    }

    public void TestPattern() // aka PatternTest()
    {
        Object testArgs[] = {
            new Double(1), new Double(3456),
            "Disk", new Date(1000000000L)
        };
        String testCases[] = {
           "Quotes '', '{', 'a' {0} '{0}'",
           "Quotes '', '{', 'a' {0,number} '{0}'",
           "'{'1,number,'#',##} {1,number,'#',##}",
           "There are {1} files on {2} at {3}.",
           "On {2}, there are {1} files, with {0,number,currency}.",
           "'{1,number,percent}', {1,number,percent},",
           "'{1,date,full}', {1,date,full},",
           "'{3,date,full}', {3,date,full},",
           "'{1,number,#,##}' {1,number,#,##}",
        };

        String testResultPatterns[] = {
            "Quotes '', '{', a {0} '{'0}",
            "Quotes '', '{', a {0,number} '{'0}",
            "'{'1,number,#,##} {1,number,'#'#,##}",
            "There are {1} files on {2} at {3}.",
            "On {2}, there are {1} files, with {0,number,currency}.",
            "'{'1,number,percent}, {1,number,percent},",
            "'{'1,date,full}, {1,date,full},",
            "'{'3,date,full}, {3,date,full},",
            "'{'1,number,#,##} {1,number,#,##}"
        };

        String testResultStrings[] = {
            "Quotes ', {, a 1 {0}",
            "Quotes ', {, a 1 {0}",
            "{1,number,#,##} #34,56",
            "There are 3,456 files on Disk at 1/12/70 5:46 AM.",
            "On Disk, there are 3,456 files, with $1.00.",
            "{1,number,percent}, 345,600%,",
            "{1,date,full}, Wednesday, December 31, 1969,",
            "{3,date,full}, Monday, January 12, 1970,",
            "{1,number,#,##} 34,56"
        };

        for (int i = 0; i < 9; ++i) {
            //it_out << "\nPat in:  " << testCases[i]);

            String buffer;
            MessageFormat form = null;
            try {
                form = new MessageFormat(testCases[i], Locale.US);
            } catch (IllegalArgumentException e1) {
                errln("MessageFormat for " + testCases[i] + " creation failed.");
                continue;
            }
            assertEquals("\"" + testCases[i] + "\".toPattern()", testResultPatterns[i], form.toPattern());

            //it_out << "Pat out: " << form.toPattern(buffer));
            StringBuffer result = new StringBuffer();
            int count = 4;
            FieldPosition fieldpos = new FieldPosition(0);
            form.format(testArgs, result, fieldpos);
            assertEquals("format", testResultStrings[i], result.toString());

            //it_out << "Result:  " << result);
    //#if 0
    //        /* TODO: Look at this test and see if this is still a valid test */
    //        logln("---------------- test parse ----------------");
    //
    //        form.toPattern(buffer);
    //        logln("MSG pattern for parse: " + buffer);
    //
    //        int parseCount = 0;
    //        Formattable* values = form.parse(result, parseCount, success);
    //        if (U_FAILURE(success)) {
    //            errln("MessageFormat failed test #5");
    //            logln(String("MessageFormat failed test #5 with error code ")+(int)success);
    //        } else if (parseCount != count) {
    //            errln("MSG count not %d as expected. Got %d", count, parseCount);
    //        }
    //        UBool failed = FALSE;
    //        for (int j = 0; j < parseCount; ++j) {
    //             if (values == 0 || testArgs[j] != values[j]) {
    //                errln(((String)"MSG testargs[") + j + "]: " + toString(testArgs[j]));
    //                errln(((String)"MSG values[") + j + "]  : " + toString(values[j]));
    //                failed = TRUE;
    //             }
    //        }
    //        if (failed)
    //            errln("MessageFormat failed test #6");
    //#endif
        }
    }

    public void TestSample() // aka sample()
    {
        MessageFormat form = null;
        StringBuffer buffer2 = new StringBuffer();
        try {
            form = new MessageFormat("There are {0} files on {1}");
        } catch (IllegalArgumentException e1) {
            errln("Sample message format creation failed.");
            return;
        }
        Object testArgs1[] = { "abc", "def" };
        FieldPosition fieldpos = new FieldPosition(0);
        assertEquals("format",
                     "There are abc files on def",
                     form.format(testArgs1, buffer2, fieldpos).toString());
    }

    public void TestStaticFormat()
    {
        Object arguments[] = {
            new Integer(7),
            new Date(871068000000L),
            "a disturbance in the Force"
        };

        assertEquals("format",
            "At 12:20:00 PM on Aug 8, 1997, there was a disturbance in the Force on planet 7.",
            MessageFormat.format("At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",
                                 arguments));
    }

    static final int FieldPosition_DONT_CARE = -1;

    public void TestSimpleFormat()
    {
        Object testArgs1[] = {new Integer(0), "MyDisk"};
        Object testArgs2[] = {new Integer(1), "MyDisk"};
        Object testArgs3[] = {new Integer(12), "MyDisk"};

        MessageFormat form = new MessageFormat(
            "The disk \"{1}\" contains {0} file(s).");

        StringBuffer string = new StringBuffer();
        FieldPosition ignore = new FieldPosition(FieldPosition_DONT_CARE);
        form.format(testArgs1, string, ignore);
        assertEquals("format",
                     "The disk \"MyDisk\" contains 0 file(s).",
                     string.toString());

        string.setLength(0);
        form.format(testArgs2, string, ignore);
        assertEquals("format",
                     "The disk \"MyDisk\" contains 1 file(s).",
                     string.toString());

        string.setLength(0);
        form.format(testArgs3, string, ignore);
        assertEquals("format",
                     "The disk \"MyDisk\" contains 12 file(s).",
                     string.toString());
    }

    public void TestMsgFormatChoice()
    {
        MessageFormat form = new MessageFormat("The disk \"{1}\" contains {0}.");
        double filelimits[] = {0,1,2};
        String filepart[] = {"no files","one file","{0,number} files"};
        ChoiceFormat fileform = new ChoiceFormat(filelimits, filepart);
        form.setFormat(1, fileform); // NOT zero, see below

        FieldPosition ignore = new FieldPosition(FieldPosition_DONT_CARE);
        StringBuffer string = new StringBuffer();
        Object testArgs1[] = {new Integer(0), "MyDisk"};
        form.format(testArgs1, string, ignore);
        assertEquals("format#1",
                     "The disk \"MyDisk\" contains no files.",
                     string.toString());

        string.setLength(0);
        Object testArgs2[] = {new Integer(1), "MyDisk"};
        form.format(testArgs2, string, ignore);
        assertEquals("format#2",
                     "The disk \"MyDisk\" contains one file.",
                     string.toString());

        string.setLength(0);
        Object testArgs3[] = {new Integer(1273), "MyDisk"};
        form.format(testArgs3, string, ignore);
        assertEquals("format#3",
                     "The disk \"MyDisk\" contains 1,273 files.",
                     string.toString());
    }

    //---------------------------------
    //  API Tests
    //---------------------------------

    public void TestClone()
    {
        MessageFormat x = new MessageFormat("There are {0} files on {1}");
        MessageFormat z = new MessageFormat("There are {0} files on {1} created");
        MessageFormat y = null;
        y = (MessageFormat)x.clone();
        if (x.equals(y) &&
            !x.equals(z) &&
            !y.equals(z) )
            logln("First test (operator ==): Passed!");
        else {
            errln("First test (operator ==): Failed!");
        }
        if ((x.equals(y) && y.equals(x)) &&
            (!x.equals(z) && !z.equals(x)) &&
            (!y.equals(z) && !z.equals(y)) )
            logln("Second test (equals): Passed!");
        else {
            errln("Second test (equals): Failed!");
        }

    }

    public void TestEquals()
    {
        MessageFormat x = new MessageFormat("There are {0} files on {1}");
        MessageFormat y = new MessageFormat("There are {0} files on {1}");
        if (!x.equals(y)) {
            errln("First test (operator ==): Failed!");
        }

    }

    public void TestNotEquals()
    {
        MessageFormat x = new MessageFormat("There are {0} files on {1}");
        MessageFormat y = new MessageFormat("There are {0} files on {1}");
        y.setLocale(Locale.FRENCH);
        if (x.equals(y)) {
            errln("First test (operator !=): Failed!");
        }
        y = new MessageFormat("There are {0} files on {1}");
        y.applyPattern("There are {0} files on {1} the disk");
        if (x.equals(y)) {
            errln("Second test (operator !=): Failed!");
        }
    }

    public void TestHashCode()
    {
        ULocale save = ULocale.getDefault();
        ULocale.setDefault(ULocale.US);

        MessageFormat x = new MessageFormat("There are {0} files on {1}");
        MessageFormat z = new MessageFormat("There are {0} files on {1}");
        MessageFormat y = null;
        y = (MessageFormat)x.clone();
        if (x.hashCode() != y.hashCode())
            errln("FAIL: identical objects have different hashcodes");
        if (x.hashCode() != z.hashCode())
            errln("FAIL: identical objects have different hashcodes");

    /* These are not errors
        y.setLocale(ULocale.FRENCH);
        if (x.hashCode() == y.hashCode())
            errln("FAIL: different objects have same hashcodes. Locale ignored");

        z.applyPattern("There are {0} files on {1} the disk");
        if (x.hashCode() == z.hashCode())
            errln("FAIL: different objects have same hashcodes. Pattern ignored");
    */

        ULocale.setDefault(save);
    }

    public void TestSetLocale()
    {
        Object arguments[] = {
            new Double(456.83),
            new Date(871068000000L),
            "deposit"
            };

        StringBuffer result = new StringBuffer();

        //String formatStr = "At {1,time} on {1,date}, you made a {2} of {0,number,currency}.";
        String formatStr = "At <time> on {1,date}, you made a {2} of {0,number,currency}.";
        // {sfb} to get $, would need Locale::US, not Locale::ENGLISH
        // Just use unlocalized currency symbol.
        //String compareStrEng = "At <time> on Aug 8, 1997, you made a deposit of $456.83.";
        String compareStrEng = "At <time> on Aug 8, 1997, you made a deposit of ";
        compareStrEng += '\u00a4';
        compareStrEng += "456.83.";
        // {sfb} to get DM, would need Locale::GERMANY, not Locale::GERMAN
        // Just use unlocalized currency symbol.
        //String compareStrGer = "At <time> on 08.08.1997, you made a deposit of 456,83 DM.";
        String compareStrGer = "At <time> on 08.08.1997, you made a deposit of ";
        compareStrGer += '\u00a4';
        compareStrGer += " 456,83.";

        MessageFormat msg = new MessageFormat(formatStr, Locale.ENGLISH);
        result.setLength(0);
        FieldPosition pos = new FieldPosition(0);
        result = msg.format(
            arguments,
            result,
            pos);
        assertEquals("format", compareStrEng, result.toString());

        msg.setLocale(Locale.ENGLISH);
        assertEquals("getLocale", Locale.ENGLISH, msg.getLocale());

        msg.setLocale(Locale.GERMAN);
        assertEquals("getLocale", Locale.GERMAN, msg.getLocale());

        msg.applyPattern(formatStr);
        result.setLength(0);
        result = msg.format(
            arguments,
            result,
            pos);
        assertEquals("format", compareStrGer, result.toString());

        //Cover getULocale()
        logln("Testing set/get ULocale ...");
        msg.setLocale(ULocale.ENGLISH);
        assertEquals("getULocale", ULocale.ENGLISH, msg.getULocale());

        msg.setLocale(ULocale.GERMAN);
        assertEquals("getULocale", ULocale.GERMAN, msg.getULocale());

        msg.applyPattern(formatStr);
        result.setLength(0);
        result = msg.format(
            arguments,
            result,
            pos);
        assertEquals("format", compareStrGer, result.toString());
    }

    public void TestFormat()
    {
        final Object ft_arr[] =
        {
            new Date(871068000000L)
        };

        StringBuffer result = new StringBuffer();

        //String formatStr = "At {1,time} on {1,date}, you made a {2} of {0,number,currency}.";
        String formatStr = "On {0,date}, it began.";
        String compareStr = "On Aug 8, 1997, it began.";

        MessageFormat msg = new MessageFormat(formatStr);
        FieldPosition fp = new FieldPosition(0);

        try {
            msg.format(new Date(871068000000L),
                       result,
                       fp);
            errln("*** MSG format without expected error code.");
        } catch (Exception e1) {
        }

        result.setLength(0);
        result = msg.format(
            ft_arr,
            result,
            fp);
        assertEquals("format", compareStr, result.toString());
    }

    public void TestParse()
    {
        String msgFormatString = "{0} =sep= {1}";
        MessageFormat msg = new MessageFormat(msgFormatString);
        String source = "abc =sep= def";
        String tmp1, tmp2;

        try {
            Object[] fmt_arr = msg.parse(source);
            if (fmt_arr.length != 2) {
                errln("*** MSG parse (ustring, count, err) count err.");
            } else {
                if (fmt_arr.length != 2) {
                    errln("*** MSG parse (ustring, parsepos., count) count err.");
                } else {
                    assertEquals("parse()[0]", "abc", fmt_arr[0]);
                    assertEquals("parse()[1]", "def", fmt_arr[1]);
                }
            }
        } catch (ParseException e1) {
            errln("*** MSG parse (ustring, count, err) error.");
        }

        ParsePosition pp = new ParsePosition(0);

        Object[] fmt_arr = msg.parse(source, pp);
        if (pp.getIndex()==0 || fmt_arr==null) {
            errln("*** MSG parse (ustring, parsepos., count) error.");
        } else {
            if (fmt_arr.length != 2) {
                errln("*** MSG parse (ustring, parsepos., count) count err.");
            } else {
                assertEquals("parse()[0]", "abc", fmt_arr[0]);
                assertEquals("parse()[1]", "def", fmt_arr[1]);
            }
        }

        pp.setIndex(0);
        Object[] fmta;

        fmta = (Object[]) msg.parseObject( source, pp );
        if (pp.getIndex() == 0) {
            errln("*** MSG parse (ustring, Object, parsepos ) error.");
        } else {
            if (fmta.length != 2) {
                errln("*** MSG parse (ustring, count, err) count err.");
            } else {
                if (fmt_arr.length != 2) {
                    errln("*** MSG parse (ustring, parsepos., count) count err.");
                } else {
                    assertEquals("parse()[0]", "abc", fmt_arr[0]);
                    assertEquals("parse()[1]", "def", fmt_arr[1]);
                }
            }
        }
    }

    /**
     * Of course, in Java there is no adopt, but we retain the same
     * method name. [alan]
     */
    public void TestAdopt()
    {
        String formatStr = "{0,date},{1},{2,number}";
        String formatStrChange = "{0,number},{1,number},{2,date}";
        MessageFormat msg = new MessageFormat(formatStr);
        MessageFormat msgCmp = new MessageFormat(formatStr);
        Format[] formats = msg.getFormats();
        Format[] formatsCmp = msgCmp.getFormats();
        Format[] formatsChg = null;
        Format[] formatsAct = null;
        Format a = null;
        Format b = null;
        String patCmp;
        String patAct;
        Format[] formatsToAdopt = null;

        if (formats==null || formatsCmp==null || (formats.length <= 0) || (formats.length != formatsCmp.length)) {
            errln("Error getting Formats");
            return;
        }

        int i;

        for (i = 0; i < formats.length; i++) {
            a = formats[i];
            b = formatsCmp[i];
            if ((a != null) && (b != null)) {
                if (!a.equals(b)) {
                    errln("a != b");
                    return;
                }
            } else if ((a != null) || (b != null)) {
                errln("(a != null) || (b != null)");
                return;
            }
        }

        msg.applyPattern( formatStrChange ); //set msg formats to something different
        formatsChg = msg.getFormats(); // tested function
        if (formatsChg==null || (formatsChg.length != formats.length)) {
            errln("Error getting Formats");
            return;
        }

        boolean diff;
        diff = true;
        for (i = 0; i < formats.length; i++) {
            a = formatsChg[i];
            b = formatsCmp[i];
            if ((a != null) && (b != null)) {
                if (a.equals(b)) {
                    logln("formatsChg == formatsCmp at index " + i);
                    diff = false;
                }
            }
        }
        if (!diff) {
            errln("*** MSG getFormats diff err.");
            return;
        }

        logln("MSG getFormats tested.");

        msg.setFormats( formatsCmp ); //tested function

        formatsAct = msg.getFormats();
        if (formatsAct==null || (formatsAct.length <=0) || (formatsAct.length != formatsCmp.length)) {
            errln("Error getting Formats");
            return;
        }

        assertEquals("msgCmp.toPattern()", formatStr, msgCmp.toPattern());
        assertEquals("msg.toPattern()", formatStr, msg.toPattern());

        for (i = 0; i < formatsAct.length; i++) {
            a = formatsAct[i];
            b = formatsCmp[i];
            if ((a != null) && (b != null)) {
                if (!a.equals(b)) {
                    errln("formatsAct != formatsCmp at index " + i);
                    return;
                }
            } else if ((a != null) || (b != null)) {
                errln("(a != null) || (b != null)");
                return;
            }
        }
        logln("MSG setFormats tested.");

        //----

        msg.applyPattern( formatStrChange ); //set msg formats to something different

        formatsToAdopt = new Format[formatsCmp.length];
        if (formatsToAdopt==null) {
            errln("memory allocation error");
            return;
        }

        for (i = 0; i < formatsCmp.length; i++) {
            if (formatsCmp[i] == null) {
                formatsToAdopt[i] = null;
            } else {
                formatsToAdopt[i] = (Format) formatsCmp[i].clone();
                if (formatsToAdopt[i]==null) {
                    errln("Can't clone format at index " + i);
                    return;
                }
            }
        }
        msg.setFormats( formatsToAdopt ); // function to test

        assertEquals("msgCmp.toPattern()", formatStr, msgCmp.toPattern());
        assertEquals("msg.toPattern()", formatStr, msg.toPattern());

        formatsAct = msg.getFormats();
        if (formatsAct==null || (formatsAct.length <=0) || (formatsAct.length != formatsCmp.length)) {
            errln("Error getting Formats");
            return;
        }

        for (i = 0; i < formatsAct.length; i++) {
            a = formatsAct[i];
            b = formatsCmp[i];
            if ((a != null) && (b != null)) {
                if (!a.equals(b)) {
                    errln("a != b");
                    return;
                }
            } else if ((a != null) || (b != null)) {
                errln("(a != null) || (b != null)");
                return;
            }
        }
        logln("MSG adoptFormats tested.");

        //---- adoptFormat

        msg.applyPattern( formatStrChange ); //set msg formats to something different

        formatsToAdopt = new Format[formatsCmp.length];
        if (formatsToAdopt==null) {
            errln("memory allocation error");
            return;
        }

        for (i = 0; i < formatsCmp.length; i++) {
            if (formatsCmp[i] == null) {
                formatsToAdopt[i] = null;
            } else {
                formatsToAdopt[i] = (Format) formatsCmp[i].clone();
                if (formatsToAdopt[i]==null) {
                    errln("Can't clone format at index " + i);
                    return;
                }
            }
        }

        for ( i = 0; i < formatsCmp.length; i++ ) {
            msg.setFormat( i, formatsToAdopt[i] ); // function to test
        }

        assertEquals("msgCmp.toPattern()", formatStr, msgCmp.toPattern());
        assertEquals("msg.toPattern()", formatStr, msg.toPattern());

        formatsAct = msg.getFormats();
        if (formatsAct==null || (formatsAct.length <=0) || (formatsAct.length != formatsCmp.length)) {
            errln("Error getting Formats");
            return;
        }

        for (i = 0; i < formatsAct.length; i++) {
            a = formatsAct[i];
            b = formatsCmp[i];
            if ((a != null) && (b != null)) {
                if (!a.equals(b)) {
                    errln("a != b");
                    return;
                }
            } else if ((a != null) || (b != null)) {
                errln("(a != null) || (b != null)");
                return;
            }
        }
        logln("MSG adoptFormat tested.");
    }

    /**
     * Verify that MessageFormat accomodates more than 10 arguments and
     * more than 10 subformats.
     */
    public void TestUnlimitedArgsAndSubformats() {
        final String pattern =
            "On {0,date} (aka {0,date,short}, aka {0,date,long}) "+
            "at {0,time} (aka {0,time,short}, aka {0,time,long}) "+
            "there were {1,number} werjes "+
            "(a {3,number,percent} increase over {2,number}) "+
            "despite the {4}''s efforts "+
            "and to delight of {5}, {6}, {7}, {8}, {9}, and {10} {11}.";
        try {
            MessageFormat msg = new MessageFormat(pattern);

            final Object ARGS[] = {
                new Date(10000000000000L),
                new Integer(1303),
                new Integer(1202),
                new Double(1303.0/1202 - 1),
                "Glimmung",
                "the printers",
                "Nick",
                "his father",
                "his mother",
                "the spiddles",
                "of course",
                "Horace"
            };

            String expected =
                "On Nov 20, 2286 (aka 11/20/86, aka November 20, 2286) "+
                "at 9:46:40 AM (aka 9:46 AM, aka 9:46:40 AM PST) "+
                "there were 1,303 werjes "+
                "(a 8% increase over 1,202) "+
                "despite the Glimmung's efforts "+
                "and to delight of the printers, Nick, his father, "+
                "his mother, the spiddles, and of course Horace.";
            String result;
            assertEquals("format", expected, msg.format(ARGS));
        } catch (IllegalArgumentException e1) {
            errln("FAIL: constructor failed");
        }
    }

    // test RBNF extensions to message format
    public void TestRBNF() {
        // WARNING: this depends on the RBNF formats for en_US
        Locale locale = Locale.US;
        String[] values = {
            // decimal values do not format completely for ordinal or duration, and
            // do not always parse, so do not include them
            "0", "1", "12", "100", "123", "1001", "123,456", "-17",
        };
        String[] formats = {
            "There are {0,spellout} files to search.",
            "There are {0,spellout,%simplified} files to search.",
            "The bogus spellout {0,spellout,%BOGUS} files behaves like the default.",
            "This is the {0,ordinal} file to search.", // TODO fix bug, ordinal does not parse
            "Searching this file will take {0,duration} to complete.",
            "Searching this file will take {0,duration,%with-words} to complete.",
        };
        final NumberFormat numFmt = NumberFormat.getInstance(locale);
        Object[] args = new Object[1];
        Number num = null;
        for (int i = 0; i < formats.length; ++i) {
            MessageFormat fmt = new MessageFormat(formats[i], locale);
            logln("Testing format pattern: '" + formats[i] + "'");
            for (int j = 0; j < values.length; ++j) {
                try {
                    num = numFmt.parse(values[j]);
                }
                catch (Exception e) {
                    throw new IllegalStateException("failed to parse test argument");
                }
                args[0] = num;
                String result = fmt.format(args);
                logln("value: " + num + " --> " + result);

                if (i != 3) { // TODO: fix this, for now skip ordinal parsing (format string at index 3)
                    try {
                        Object[] parsedArgs = fmt.parse(result);
                        if (parsedArgs.length != 1) {
                            errln("parse returned " + parsedArgs.length + " args");
                        } else if (!parsedArgs[0].equals(num)) {
                            errln("parsed argument " + parsedArgs[0] + " != " + num);
                        }
                    }
                    catch (Exception e) {
                        errln("parse of '" + result + " returned exception: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void TestSetGetFormats()
    {
        Object arguments[] = {
            new Double(456.83),
            new Date(871068000000L),
            "deposit"
            };

        StringBuffer result = new StringBuffer();

        String formatStr = "At <time> on {1,date}, you made a {2} of {0,number,currency}.";
        // original expected format result
        String compareStr = "At <time> on Aug 8, 1997, you made a deposit of $456.83.";
        // the date being German-style, but the currency being English-style
        String compareStr2 = "At <time> on 08.08.1997, you made a deposit of ";
        compareStr2 += '\u00a4';
        compareStr2 += "456.83.";
        // both date and currency formats are German-style
        String compareStr3 = "At <time> on 08.08.1997, you made a deposit of ";
        compareStr3 += '\u00a4';
        compareStr3 += " 456,83.";

        MessageFormat msg = new MessageFormat(formatStr, ULocale.US);
        result.setLength(0);
        FieldPosition pos = new FieldPosition(0);
        result = msg.format(
            arguments,
            result,
            pos);
        assertEquals("format", compareStr, result.toString());

        // constructs a Format array with a English-style Currency formatter
        //                            and a German-style Date formatter
        //      might not meaningful, just for testing setFormatsByArgIndex
        Format[] fmts = new Format[] {
            NumberFormat.getCurrencyInstance(ULocale.ENGLISH),
            DateFormat.getDateInstance(DateFormat.DEFAULT, ULocale.GERMAN)
            };

        msg.setFormatsByArgumentIndex(fmts);
        result.setLength(0);
        pos = new FieldPosition(0);
        result = msg.format(
            arguments,
            result,
            pos);
        assertEquals("format", compareStr2, result.toString());

        // Construct a German-style Currency formatter, replace the corresponding one
        // Thus both formatters should format objects with German-style
        Format newFmt = NumberFormat.getCurrencyInstance(ULocale.GERMAN);
        msg.setFormatByArgumentIndex(0, newFmt);
        result.setLength(0);
        pos = new FieldPosition(0);
        result = msg.format(
            arguments,
            result,
            pos);
        assertEquals("format", compareStr3, result.toString());

        // verify getFormatsByArgumentIndex
        //   you should got three formats by that
        //          - DecimalFormat     locale: de
        //          - SimpleDateFormat  locale: de
        //          - null
        Format[] fmts2 = msg.getFormatsByArgumentIndex();
        assertEquals("1st subformmater: Format Class", "com.ibm.icu.text.DecimalFormat", fmts2[0].getClass().getName());
        assertEquals("1st subformmater: its Locale", ULocale.GERMAN, ((UFormat)fmts2[0]).getLocale(ULocale.VALID_LOCALE));
        assertEquals("2nd subformatter: Format Class", "com.ibm.icu.text.SimpleDateFormat", fmts2[1].getClass().getName());
        assertEquals("2nd subformmater: its Locale", ULocale.GERMAN, ((UFormat)fmts2[1]).getLocale(ULocale.VALID_LOCALE));
        assertTrue("The third subFormatter is null", null == fmts2[2]);
    }

    // Test the fix pattern api
    public void TestAutoQuoteApostrophe() {
        final String[] patterns = { // new pattern, expected pattern
            "'", "''",
            "''", "''",
            "'{", "'{'",
            "' {", "'' {",
            "'a", "''a",
            "'{'a", "'{'a",
            "'{a'", "'{a'",
            "'{}", "'{}'",
            "{'", "{'",
            "{'a", "{'a",
            "{'a{}'a}'a", "{'a{}'a}''a",
	    "'}'", "'}'",
	    "'} '{'}'", "'} '{'}''",
	    "'} {{{''", "'} {{{'''",
        };
        for (int i = 0; i < patterns.length; i += 2) {
            assertEquals("[" + (i/2) + "] \"" + patterns[i] + "\"", patterns[i+1], MessageFormat.autoQuoteApostrophe(patterns[i]));
        }
    }
}