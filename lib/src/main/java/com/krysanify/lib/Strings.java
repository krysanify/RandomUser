package com.krysanify.lib;

import static java.lang.Character.isDigit;
import static java.lang.Character.isSpaceChar;
import static java.lang.Character.isWhitespace;

/**
 * Utility class for String operations
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Strings {
    /**
     * @param str a {@link String} to check
     * @return true if given String consists of whitespaces only, otherwise false
     */
    public static boolean isBlank(String str) {
        final int len = str.length();
        if (0 == len) return true;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!isWhitespace(c) && !isSpaceChar(c)) return false;
        }
        return true;
    }

    /**
     * @param str a {@link String} to check
     * @return true if given String consists of digits only, otherwise false
     */
    public static boolean isNumber(String str) {
        final int len = str.length();
        if (0 == len) return false;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!isDigit(c)) return false;
        }
        return true;
    }
}
