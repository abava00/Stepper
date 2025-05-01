package com.coreyd97.stepper.preferences.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class LimitedLengthDocumentFilter extends DocumentFilter {
    private final int maxLength;

    public LimitedLengthDocumentFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null && (fb.getDocument().getLength() + string.length()) <= maxLength) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
        if (string != null && (fb.getDocument().getLength() - length + string.length()) <= maxLength) {
            super.replace(fb, offset, length, string, attrs);
        }
    }
}