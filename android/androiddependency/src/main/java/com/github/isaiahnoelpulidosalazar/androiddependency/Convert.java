package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * A utility class providing static methods for common type and format conversions,
 * including string formatting, Base64 encoding/decoding, date formatting, and
 * safe numeric parsing.
 */
public class Convert {

    /**
     * Capitalizes the first character of the given string, leaving the rest unchanged.
     *
     * <p>Useful for converting camelCase or all-lowercase identifiers into a human-readable
     * display name.</p>
     *
     * <p>Example: {@code "john"} → {@code "John"}</p>
     *
     * @param str the input string; must be non-null and non-empty
     * @return the input string with its first character uppercased
     */
    public static String toRealName(String str){
        return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
    }

    /**
     * Encodes the given string to a Base64 representation using UTF-8 encoding.
     *
     * @param str the plain-text string to encode
     * @return the Base64-encoded string (may include a trailing newline due to {@link Base64#DEFAULT})
     */
    public static String toBase64(String str){
        return new String(Base64.encode(str.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
    }

    /**
     * Decodes a Base64-encoded string back to its original plain-text form.
     *
     * @param str the Base64-encoded string to decode
     * @return the decoded plain-text string, or {@code null} if decoding fails
     */
    public static String fromBase64(String str){
        try {
            return new String(Base64.decode(str, Base64.DEFAULT));
        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Formats a {@link Date} as a string in {@code MM/DD/YYYY} format.
     *
     * @param date the date to format
     * @return a string such as {@code "5/4/2026"} for May 4, 2026
     * @deprecated {@link Date#getMonth()}, {@link Date#getDate()}, and {@link Date#getYear()}
     *             are deprecated. Consider using {@link java.util.Calendar} or
     *             {@code java.time.LocalDate} instead.
     */
    @Deprecated
    public static String dateToMMDDYY(Date date){
        return (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900);
    }

    /**
     * Formats a {@link Date} as a string in {@code DD/MM/YYYY} format.
     *
     * @param date the date to format
     * @return a string such as {@code "4/5/2026"} for May 4, 2026
     * @deprecated {@link Date#getMonth()}, {@link Date#getDate()}, and {@link Date#getYear()}
     *             are deprecated. Consider using {@link java.util.Calendar} or
     *             {@code java.time.LocalDate} instead.
     */
    @Deprecated
    public static String dateToDDMMYY(Date date){
        return date.getDate() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900);
    }

    /**
     * Formats a {@link Date} as a string in {@code YYYY/MM/DD} format.
     *
     * @param date the date to format
     * @return a string such as {@code "2026/5/4"} for May 4, 2026
     * @deprecated {@link Date#getMonth()}, {@link Date#getDate()}, and {@link Date#getYear()}
     *             are deprecated. Consider using {@link java.util.Calendar} or
     *             {@code java.time.LocalDate} instead.
     */
    @Deprecated
    public static String dateToYYMMDD(Date date){
        return (date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate();
    }

    /**
     * Safely parses a string to an {@code int}.
     *
     * <p>Returns {@code 0} if parsing fails (e.g., the string is not a valid integer).</p>
     *
     * @param str the string to parse
     * @return the parsed integer value, or {@code 0} on failure
     */
    public static int toInt(String str){
        try {
            return Integer.parseInt(str);
        } catch (Exception exception){
            exception.printStackTrace();
            return 0;
        }
    }

    /**
     * Safely parses a string to a {@code float}.
     *
     * <p>Returns {@code 0} if parsing fails (e.g., the string is not a valid float).</p>
     *
     * @param str the string to parse
     * @return the parsed float value, or {@code 0} on failure
     */
    public static float toFloat(String str){
        try {
            return Float.parseFloat(str);
        } catch (Exception exception){
            exception.printStackTrace();
            return 0;
        }
    }

    /**
     * Safely parses a string to a {@code double}.
     *
     * <p>Returns {@code 0} if parsing fails (e.g., the string is not a valid double).</p>
     *
     * @param str the string to parse
     * @return the parsed double value, or {@code 0} on failure
     */
    public static double toDouble(String str){
        try {
            return Double.parseDouble(str);
        } catch (Exception exception){
            exception.printStackTrace();
            return 0;
        }
    }

    /**
     * Converts any object to its string representation using {@link String#valueOf(Object)}.
     *
     * <p>Returns the string {@code "null"} if the object is {@code null}.</p>
     *
     * @param obj the object to convert
     * @return the string representation of {@code obj}
     */
    public static String toString(Object obj){
        return String.valueOf(obj);
    }
}