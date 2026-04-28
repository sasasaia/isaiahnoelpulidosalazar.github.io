package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.appcompat.content.res.AppCompatResources;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Check {
    public static class Email {

        static List<String> validDomainNames = new ArrayList<>();
        static List<String> validDomainExtensions = new ArrayList<>();
        static List<String> validDomains = new ArrayList<>();
        static boolean shouldUseFullDomain = false;

        public static void addValidDomainName(String str){
            validDomainNames.add(str);
        }

        public static void addValidDomainExtension(String str){
            validDomainExtensions.add(str);
        }

        public static void addValidDomain(String str){
            validDomains.add(str);
        }

        public static void shouldUseFullDomain(){
            shouldUseFullDomain = true;
        }

        public static void shouldUseFullDomain(boolean bool){
            shouldUseFullDomain = bool;
        }

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

    public static boolean equalDrawableAndBitmap(Context context, int resourceId, Bitmap bitmap){
        BitmapDrawable drawable = (BitmapDrawable) AppCompatResources.getDrawable(context, resourceId);
        return drawable.getBitmap().sameAs(bitmap);
    }

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

    public static boolean hasSpaces(String str){
        for (char a : str.toCharArray()){
            if (a == ' '){
                return true;
            }
        }
        return false;
    }

    public static double howManySecondsLeft(Date now, Date until){
        return new Date(until.getTime() - now.getTime()).getTime() / 1000.0;
    }

    public static double howManyMinutesLeft(Date now, Date until){
        return new Date(until.getTime() - now.getTime()).getTime() / 60000.0;
    }

    public static double howManyHoursLeft(Date now, Date until){
        return new Date(until.getTime() - now.getTime()).getTime() / 60000.0 / 60.0;
    }

    public static double howManyDaysLeft(Date now, Date until){
        return new Date(until.getTime() - now.getTime()).getTime() / 60000.0 / 60.0 / 24.0;
    }
}
