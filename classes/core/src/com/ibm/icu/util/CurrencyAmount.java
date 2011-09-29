﻿/*
**********************************************************************
* Copyright (c) 2004-2011, International Business Machines
* Corporation and others.  All Rights Reserved.
**********************************************************************
* Author: Alan Liu
* Created: April 12, 2004
* Since: ICU 3.0
**********************************************************************
*/
package com.ibm.icu.util;


/**
 * An amount of currency, consisting of a Number and a Currency.
 * CurrencyAmount objects are immutable.
 *
 * @see java.lang.Number
 * @see Currency
 * @author Alan Liu
 * @stable ICU 3.0
 */
public class CurrencyAmount extends Measure {
    
    /**
     * Constructs a new object given a number and a currency.
     * @param number the number
     * @param currency the currency
     * @stable ICU 3.0
     */
    public CurrencyAmount(Number number, Currency currency) {
        super(number, currency);
    }

    /**
     * Constructs a new object given a double value and a currency.
     * @param number a double value
     * @param currency the currency
     * @stable ICU 3.0
     */
    public CurrencyAmount(double number, Currency currency) {
        super(new Double(number), currency);
    }    
    
    /**
     * Returns the currency of this object.
     * @return this object's Currency
     * @stable ICU 3.0
     */
    public Currency getCurrency() {
        return (Currency) getUnit();
    }
}
