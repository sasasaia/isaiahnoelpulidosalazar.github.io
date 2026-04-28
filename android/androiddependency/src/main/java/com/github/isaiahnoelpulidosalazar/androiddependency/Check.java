package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The class {@code Check} checks a given argument if it is valid
 * based on the method used.
 * @author Isaiah Noel Salazar
 */
public class Check {
    public static class Email {

        static List<String> validDomainNames = new ArrayList<>();
        static List<String> validDomainExtensions = new ArrayList<>();
        static List<String> validDomains = new ArrayList<>();
        static boolean shouldUseFullDomain = false;

        /**
         * Adds a {@code String} object for valid domain names.
         * @param str a {@code String} object.
         */
        public static void addValidDomainName(String str){
            validDomainNames.add(str);
        }

        /**
         * Adds a {@code String} object for valid domain extensions.
         * @param str a {@code String} object.
         */
        public static void addValidDomainExtension(String str){
            validDomainExtensions.add(str);
        }

        /**
         * Adds a {@code String} object for valid domains.
         * @param str a {@code String} object.
         */
        public static void addValidDomain(String str){
            validDomains.add(str);
        }

        /**
         * Sets the {@code shouldUseFullDomain} value to {@code true}.
         */
        public static void shouldUseFullDomain(){
            shouldUseFullDomain = true;
        }

        /**
         * Sets the {@code shouldUseFullDomain} value using a {@code boolean} value.
         * @param bool a {@code boolean} value.
         */
        public static void shouldUseFullDomain(boolean bool){
            shouldUseFullDomain = bool;
        }

        /**
         * Checks if a given {@code String} is a valid email.
         * Adding valid full domains or domain names and extensions is a pre-requisite, else
         * it will not work.
         * @param str a {@code String} object.
         * @return {@code true} if it is valid, {@code false} otherwise.
         */
        public static boolean isValid(String str){
            if (shouldUseFullDomain){
                try {
                    String[] domain = str.split("@");
                    return validDomains.contains(domain[1]);
                } catch (Exception exception){
                    return false;
                }
            } else {
                try {
                    String[] domain = str.split("@");
                    String domainName = domain[1].split("\\.")[0];
                    String domainExtension = domain[1].split("\\.")[1];
                    return validDomainNames.contains(domainName) && validDomainExtensions.contains(domainExtension);
                } catch (Exception exception){
                    return false;
                }
            }
        }
    }

    /**
     * Converts a {@code Drawable} to a {@code Bitmap} and checks if it is equal to each other.
     * @param context a {@code Context} object.
     * @param resourceId an {@code Integer} representing the resource id of the drawable.
     * @param bitmap a {@code Bitmap} object.
     * @return {@code true} if it is equal, {@code false} otherwise.
     */
    public static boolean equalDrawableAndBitmap(Context context, int resourceId, Bitmap bitmap){
        BitmapDrawable drawable = (BitmapDrawable) AppCompatResources.getDrawable(context, resourceId);
        return drawable.getBitmap().sameAs(bitmap);
    }

    /**
     * Checks if a {@code String} contains any symbols.
     * @param str a {@code String} object.
     * @return {@code true} if it has symbols, {@code false} otherwise.
     */
    public static boolean hasSymbols(String str){
        String symbols = "~`!@#$%^&*()_+-=[]{}\\|'\";:,.<>/?";
        for (char a : str.toCharArray()){
            for (char b : symbols.toCharArray()){
                if (a == b){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a {@code String} contains any numbers.
     * @param str a {@code String} object.
     * @return {@code true} if it has numbers, {@code false} otherwise.
     */
    public static boolean hasNumbers(String str){
        String numbers = "0123456789";
        for (char a : str.toCharArray()){
            for (char b : numbers.toCharArray()){
                if (a == b){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a {@code String} contains any spaces.
     * @param str a {@code String} object.
     * @return {@code true} if it has spaces, {@code false} otherwise.
     */
    public static boolean hasSpaces(String str){
        for (char a : str.toCharArray()){
            if (a == ' '){
                return true;
            }
        }
        return false;
    }

    /**
     * Compares two {@code Date} objects to see how many seconds are left.
     * @param now the first {@code Date} object.
     * @param until the second {@code Date} object.
     * @return a {@code double} value representing the number of seconds left.
     */
    public static double howManySecondsLeft(Date now, Date until){
        return new Date(until.getTime() - now.getTime()).getTime() / 1000.0;
    }

    /**
     * Compares two {@code Date} objects to see how many minutes are left.
     * @param now the first {@code Date} object.
     * @param until the second {@code Date} object.
     * @return a {@code double} value representing the number of minutes left.
     */
    public static double howManyMinutesLeft(Date now, Date until){
        return new Date(until.getTime() - now.getTime()).getTime() / 60000.0;
    }

    /**
     * Compares two {@code Date} objects to see how many hours are left.
     * @param now the first {@code Date} object.
     * @param until the second {@code Date} object.
     * @return a {@code double} value representing the number of hours left.
     */
    public static double howManyHoursLeft(Date now, Date until){
        return new Date(until.getTime() - now.getTime()).getTime() / 60000.0 / 60.0;
    }

    /**
     * Compares two {@code Date} objects to see how many days are left.
     * @param now the first {@code Date} object.
     * @param until the second {@code Date} object.
     * @return a {@code double} value representing the number of days left.
     */
    public static double howManyDaysLeft(Date now, Date until){
        return new Date(until.getTime() - now.getTime()).getTime() / 60000.0 / 60.0 / 24.0;
    }
}
