package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Convert {

    public static String toRealName(String str){
        return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
    }

    public static String toBase64(String str){
        return new String(Base64.encode(str.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
    }

    public static String fromBase64(String str){
        try {
            return new String(Base64.decode(str, Base64.DEFAULT));
        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    public static String dateToMMDDYY(Date date){
        return (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900);
    }

    public static String dateToDDMMYY(Date date){
        return date.getDate() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900);
    }

    public static String dateToYYMMDD(Date date){
        return (date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate();
    }

    public static int toInt(String str){
        try {
            return Integer.parseInt(str);
        } catch (Exception exception){
            exception.printStackTrace();
            return 0;
        }
    }

    public static float toFloat(String str){
        try {
            return Float.parseFloat(str);
        } catch (Exception exception){
            exception.printStackTrace();
            return 0;
        }
    }

    public static double toDouble(String str){
        try {
            return Double.parseDouble(str);
        } catch (Exception exception){
            exception.printStackTrace();
            return 0;
        }
    }

    public static String toString(Object obj){
        return String.valueOf(obj);
    }
}
