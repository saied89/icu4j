/*
 *******************************************************************************
 * Copyright (C) 1996-2000, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 *
 * $Source: /xsrl/Nsvn/icu/icu4j/src/com/ibm/icu/text/BreakTransliterator.java,v $ 
 * $Date: 2002/07/13 03:27:09 $ 
 * $Revision: 1.1 $
 *
 *****************************************************************************************
 */
package com.ibm.icu.text;
//import com.ibm.icu.dev.demo.impl.*;
//import com.ibm.icu.lang.*;
import com.ibm.icu.text.Transliterator;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.UnicodeFilter;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.impl.UCharacterIterator;
import java.util.Locale;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
//import java.util.*;
//import java.util.ArrayList;
//import java.io.*;

/**
 * Inserts the specified characters at word breaks. To restrict it to particular characters, use a filter.
 * TODO: this is an internal class, and only temporary. Remove it once we have \b notation in Transliterator.
 */
final class BreakTransliterator extends Transliterator {
    private BreakIterator bi;
    private String insertion;
    private int[] boundaries = new int[50];
    private int boundaryCount = 0;
    
    public BreakTransliterator(String ID, UnicodeFilter filter, BreakIterator bi, String insertion) {
        super(ID, filter);
        if (bi == null) bi = BreakIterator.getWordInstance(new Locale("th", "TH"));
        this.bi = bi;
        this.insertion = insertion;
    }
    
    public BreakTransliterator(String ID, UnicodeFilter filter) {
        this(ID, filter, null, " ");
    }
    
    public String getInsertion() {
        return insertion;
    }
    
    public void setInsertion(String insertion) {
        this.insertion = insertion;
    }
    
    public BreakIterator getBreakIterator() {
        return bi;
    }
    
    public void setBreakIterator(BreakIterator bi) {
        this.bi = bi;
    }
    
    static final int LETTER_OR_MARK_MASK = 
          (1<<Character.UPPERCASE_LETTER)
        | (1<<Character.LOWERCASE_LETTER)
        | (1<<Character.TITLECASE_LETTER)
        | (1<<Character.MODIFIER_LETTER)
        | (1<<Character.OTHER_LETTER)
        | (1<<Character.COMBINING_SPACING_MARK)
        | (1<<Character.NON_SPACING_MARK)
        | (1<<Character.ENCLOSING_MARK)
        ;
    protected void handleTransliterate(Replaceable text, Position pos, boolean incremental) {
        boundaryCount = 0;
        int boundary = 0;
        bi.setText(new ReplaceableCharacterIterator(text, pos.start, pos.limit, pos.start));
        // TODO: fix clumsy workaround used below.
        /*
        char[] tempBuffer = new char[text.length()];
        text.getChars(0, text.length(), tempBuffer, 0);
        bi.setText(new StringCharacterIterator(new String(tempBuffer), pos.start, pos.limit, pos.start));
        */
        // end debugging
        
        // To make things much easier, we will stack the boundaries, and then insert at the end.
        // generally, we won't need too many, since we will be filtered.
        
        for(boundary = bi.first(); boundary != bi.DONE && boundary < pos.limit; boundary = bi.next()) {
            if (boundary == 0) continue;
            // HACK: Check to see that preceeding item was a letter

            int cp = UTF16.charAt(text, boundary-1);
            int type = UCharacter.getType(cp);
            //System.out.println(Integer.toString(cp,16) + " (before): " + type);
            if (((1<<type) & LETTER_OR_MARK_MASK) == 0) continue;
            
            cp = UTF16.charAt(text, boundary);
            type = UCharacter.getType(cp);
            //System.out.println(Integer.toString(cp,16) + " (after): " + type);
            if (((1<<type) & LETTER_OR_MARK_MASK) == 0) continue;
            
            if (boundaryCount >= boundaries.length) {       // realloc if necessary
                int[] temp = new int[boundaries.length * 2];
                System.arraycopy(boundaries, 0, temp, 0, boundaries.length);
                boundaries = temp;
            }
            
            boundaries[boundaryCount++] = boundary;
            //System.out.println(boundary);
        }
        
        int delta = 0;
        int lastBoundary = 0;
        
        if (boundaryCount != 0) { // if we found something, adjust
            delta = boundaryCount * insertion.length();
            lastBoundary = boundaries[boundaryCount-1];
            
            // we do this from the end backwards, so that we don't have to keep updating.
            
            while (boundaryCount > 0) {
                boundary = boundaries[--boundaryCount];
                text.replace(boundary, boundary, insertion);
            }
        }
        
        // Now fix up the return values
        pos.contextLimit += delta;
        pos.limit += delta;
        pos.start = incremental ? lastBoundary + delta : pos.limit;
    }
    
    
    /**
     * Registers standard variants with the system.  Called by
     * Transliterator during initialization.
     */
    static void register() {
        // false means that it is invisible
        Transliterator trans = new BreakTransliterator("Any-BreakInternal", null);
        Transliterator.registerInstance(trans, false);
        /*
        Transliterator.registerFactory("Any-Break", new Transliterator.Factory() {
            public Transliterator getInstance(String ID) {
                return new BreakTransliterator("Any-Break", null);
            }
        });
        */
    }
    
    // Hack, just to get a real character iterator.
    
    static final class ReplaceableCharacterIterator implements CharacterIterator
    {
        private Replaceable text;
        private int begin;
        private int end;
        // invariant: begin <= pos <= end
        private int pos;

        /**
        * Constructs an iterator with an initial index of 0.
        */
        public ReplaceableCharacterIterator(Replaceable text)
        {
            this(text, 0);
        }

        /**
        * Constructs an iterator with the specified initial index.
        *
        * @param  text   The String to be iterated over
        * @param  pos    Initial iterator position
        */
        public ReplaceableCharacterIterator(Replaceable text, int pos)
        {
        this(text, 0, text.length(), pos);
        }

        /**
        * Constructs an iterator over the given range of the given string, with the
        * index set at the specified position.
        *
        * @param  text   The String to be iterated over
        * @param  begin  Index of the first character
        * @param  end    Index of the character following the last character
        * @param  pos    Initial iterator position
        */
        public ReplaceableCharacterIterator(Replaceable text, int begin, int end, int pos) {
            if (text == null) {
                throw new NullPointerException();
            }
            this.text = text;

            if (begin < 0 || begin > end || end > text.length()) {
                throw new IllegalArgumentException("Invalid substring range");
            }

            if (pos < begin || pos > end) {
                throw new IllegalArgumentException("Invalid position");
            }

            this.begin = begin;
            this.end = end;
            this.pos = pos;
        }

        /**
        * Reset this iterator to point to a new string.  This package-visible
        * method is used by other java.text classes that want to avoid allocating
        * new ReplaceableCharacterIterator objects every time their setText method
        * is called.
        *
        * @param  text   The String to be iterated over
        */
        public void setText(Replaceable text) {
            if (text == null) {
                throw new NullPointerException();
            }
            this.text = text;
            this.begin = 0;
            this.end = text.length();
            this.pos = 0;
        }

        /**
        * Implements CharacterIterator.first() for String.
        * @see CharacterIterator#first
        */
        public char first()
        {
            pos = begin;
            return current();
        }

        /**
        * Implements CharacterIterator.last() for String.
        * @see CharacterIterator#last
        */
        public char last()
        {
            if (end != begin) {
                pos = end - 1;
            } else {
                pos = end;
            }
            return current();
        }

        /**
        * Implements CharacterIterator.setIndex() for String.
        * @see CharacterIterator#setIndex
        */
        public char setIndex(int p)
        {
        if (p < begin || p > end) {
                throw new IllegalArgumentException("Invalid index");
        }
            pos = p;
            return current();
        }

        /**
        * Implements CharacterIterator.current() for String.
        * @see CharacterIterator#current
        */
        public char current()
        {
            if (pos >= begin && pos < end) {
                return text.charAt(pos);
            }
            else {
                return DONE;
            }
        }

        /**
        * Implements CharacterIterator.next() for String.
        * @see CharacterIterator#next
        */
        public char next()
        {
            if (pos < end - 1) {
                pos++;
                return text.charAt(pos);
            }
            else {
                pos = end;
                return DONE;
            }
        }

        /**
        * Implements CharacterIterator.previous() for String.
        * @see CharacterIterator#previous
        */
        public char previous()
        {
            if (pos > begin) {
                pos--;
                return text.charAt(pos);
            }
            else {
                return DONE;
            }
        }

        /**
        * Implements CharacterIterator.getBeginIndex() for String.
        * @see CharacterIterator#getBeginIndex
        */
        public int getBeginIndex()
        {
            return begin;
        }

        /**
        * Implements CharacterIterator.getEndIndex() for String.
        * @see CharacterIterator#getEndIndex
        */
        public int getEndIndex()
        {
            return end;
        }

        /**
        * Implements CharacterIterator.getIndex() for String.
        * @see CharacterIterator#getIndex
        */
        public int getIndex()
        {
            return pos;
        }

        /**
        * Compares the equality of two ReplaceableCharacterIterator objects.
        * @param obj the ReplaceableCharacterIterator object to be compared with.
        * @return true if the given obj is the same as this
        * ReplaceableCharacterIterator object; false otherwise.
        */
        public boolean equals(Object obj)
        {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ReplaceableCharacterIterator)) {
                return false;
            }

            ReplaceableCharacterIterator that = (ReplaceableCharacterIterator) obj;

            if (hashCode() != that.hashCode()) {
                return false;
            }
            if (!text.equals(that.text)) {
                return false;
            }
            if (pos != that.pos || begin != that.begin || end != that.end) {
                return false;
            }
            return true;
        }

        /**
        * Computes a hashcode for this iterator.
        * @return A hash code
        */
        public int hashCode()
        {
            return text.hashCode() ^ pos ^ begin ^ end;
        }

        /**
        * Creates a copy of this iterator.
        * @return A copy of this
        */
        public Object clone()
        {
            try {
                ReplaceableCharacterIterator other
                = (ReplaceableCharacterIterator) super.clone();
                return other;
            }
            catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
        }

    }
        
}