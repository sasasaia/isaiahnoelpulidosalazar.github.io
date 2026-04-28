package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * The class {@code Convert} converts a given argument to another type.
 * @author Isaiah Noel Salazar
 */
public class Convert {

    /**
     * Capitalizes the first character of a {@code String} and copies the rest.
     * @param str a {@code String} object.
     * @return a {@code String} with the first character capitalized.
     */
    public static String toRealName(String str){
        return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
    }

    /**
     * Converts a {@code String} to its Base64 representation.
     * @param str a {@code String} object.
     * @return a {@code String} object representing the Base64 version of the {@code String} argument.
     */
    public static String toBase64(String str){
        return new String(Base64.encode(str.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
    }

    /**
     * Converts a Base64 {@code String} to its normal {@code String} representation.
     * @param str a {@code String} object.
     * @return a {@code String} object representing the normal version of the Base64 {@code String} argument.
     * @throws Exception if {@code str} is not a valid Base64 {@code String}.
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
     * Converts a {@code Date} object to a {@code String} in the format of "MM/DD/YY".
     * @param date a {@code Date} object.
     * @return a {@code String} in the format of "MM/DD/YY".
     */
    public static String dateToMMDDYY(Date date){
        return (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900);
    }

    /**
     * Converts a {@code Date} object to a {@code String} in the format of "DD/MM/YY".
     * @param date a {@code Date} object.
     * @return a {@code String} in the format of "DD/MM/YY".
     */
    public static String dateToDDMMYY(Date date){
        return date.getDate() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900);
    }

    /**
     * Converts a {@code Date} object to a {@code String} in the format of "YY/MM/DD".
     * @param date a {@code Date} object.
     * @return a {@code String} in the format of "YY/MM/DD".
     */
    public static String dateToYYMMDD(Date date){
        return (date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate();
    }

    /*
    Note:
        The methods below exists for no reason at all. You don't need to use them. They are only
        renamed methods from already working methods.
     */

    /**
     * Converts a {@code String} to an {@code int}.
     * @param str a {@code String} object.
     * @return an {@code int} representing the {@code String} argument.
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
     * Converts a {@code String} to a {@code float}.
     * @param str a {@code String} object.
     * @return a {@code float} representing the {@code String} argument.
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
     * Converts a {@code String} to a {@code double}.
     * @param str a {@code String} object.
     * @return a {@code double} representing the {@code String} argument.
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
     * Converts an {@code Object} to a {@code String}.
     * @param obj an {@code Object}.
     * @return a {@code String} representing the {@code Object} argument.
     */
    public static String toString(Object obj){
        return String.valueOf(obj);
    }
}
