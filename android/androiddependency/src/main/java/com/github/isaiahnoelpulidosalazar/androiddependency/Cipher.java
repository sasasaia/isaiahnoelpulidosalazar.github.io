package com.github.isaiahnoelpulidosalazar.androiddependency;

public class Cipher {
    static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String transpositionCipher(String text){
        text = text.replace(" ", "");
        String temp = "";
        String temp1 = "";
        int counter = 0;
        while (counter < text.length()){
            if (counter % 2 == 0){
                temp += String.valueOf(text.toUpperCase().toCharArray()[counter]).toUpperCase();
            } else {
                temp1 += String.valueOf(text.toUpperCase().toCharArray()[counter]).toUpperCase();
            }
            counter++;
        }
        return temp + temp1;
    }

    public static String giovanniCipher(String text, String keyword, String keyLetter){
        String temp = "";
        String temp1 = "";
        String temp2 = "";
        String cipher = "";
        for (int a = 0; a < keyword.toUpperCase().length(); a++){
            if (!temp.contains(String.valueOf(keyword.toUpperCase().toCharArray()[a]))){
                temp += keyword.toUpperCase().toCharArray()[a];
            }
        }
        temp1 = temp;
        for (int a = 0; a < alphabet.length(); a++){
            if (!temp1.contains(String.valueOf(alphabet.toCharArray()[a]))){
                temp1 += alphabet.toCharArray()[a];
            }
        }
        for (int a = temp1.length() - alphabet.indexOf(keyLetter.toUpperCase()); a < temp1.length(); a++){
            temp2 += temp1.toCharArray()[a];
        }
        for (int a = 0; a < temp1.length() - alphabet.indexOf(keyLetter.toUpperCase()); a++){
            temp2 += temp1.toCharArray()[a];
        }
        for (int a = 0; a < text.toUpperCase().length(); a++){
            if (text.toUpperCase().toCharArray()[a] == ' '){
                cipher += " ";
            } else if (!alphabet.contains(String.valueOf(text.toUpperCase().toCharArray()[a]))){
                cipher += text.toUpperCase().toCharArray()[a];
            } else {
                for (int b = 0; b < alphabet.length(); b++){
                    if (text.toUpperCase().toCharArray()[a] == alphabet.toCharArray()[b]){
                        cipher += temp2.toCharArray()[b];
                    }
                }
            }
        }
        return cipher;
    }

    public static String keywordCipher(String text, String keyword){
        String temp = "";
        String temp1 = "";
        String cipher = "";
        for (int a = 0; a < keyword.toUpperCase().length(); a++){
            if (!temp.contains(String.valueOf(keyword.toUpperCase().toCharArray()[a]))){
                temp += keyword.toUpperCase().toCharArray()[a];
            }
        }
        temp1 = temp;
        for (int a = 0; a < alphabet.length(); a++){
            if (!temp1.contains(String.valueOf(alphabet.toCharArray()[a]))){
                temp1 += alphabet.toCharArray()[a];
            }
        }
        for (int a = 0; a < text.toUpperCase().length(); a++){
            if (text.toUpperCase().toCharArray()[a] == ' '){
                cipher += " ";
            } else if (!alphabet.contains(String.valueOf(text.toUpperCase().toCharArray()[a]))){
                cipher += text.toUpperCase().toCharArray()[a];
            } else {
                for (int b = 0; b < alphabet.length(); b++){
                    if (text.toUpperCase().toCharArray()[a] == alphabet.toCharArray()[b]){
                        cipher += temp1.toCharArray()[b];
                    }
                }
            }
        }
        return cipher;
    }

    public static String caesarCipher(String text, int shift){
        String temp = "";
        String cipher = "";
        for (int a = shift; a < alphabet.length(); a++){
            temp += alphabet.toCharArray()[a];
        }
        for (int a = 0; a < shift; a++){
            temp += alphabet.toCharArray()[a];
        }
        for (int a = 0; a < text.toUpperCase().length(); a++){
            if (text.toUpperCase().toCharArray()[a] == ' '){
                cipher += " ";
            } else if (!alphabet.contains(String.valueOf(text.toUpperCase().toCharArray()[a]))){
                cipher += text.toUpperCase().toCharArray()[a];
            } else {
                for (int b = 0; b < alphabet.length(); b++){
                    if (text.toUpperCase().toCharArray()[a] == alphabet.toCharArray()[b]){
                        cipher += temp.toCharArray()[b];
                    }
                }
            }
        }
        return cipher;
    }
}
