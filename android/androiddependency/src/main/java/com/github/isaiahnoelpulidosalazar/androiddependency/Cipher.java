package com.github.isaiahnoelpulidosalazar.androiddependency;

/**
 * A utility class providing static methods for classical text encryption ciphers.
 *
 * <p>Supported ciphers:</p>
 * <ul>
 *   <li>{@link #transpositionCipher(String)} — splits text into two interleaved halves</li>
 *   <li>{@link #caesarCipher(String, int)} — shifts each letter by a fixed amount</li>
 *   <li>{@link #keywordCipher(String, String)} — substitutes using a keyword-derived alphabet</li>
 *   <li>{@link #giovanniCipher(String, String, String)} — keyword cipher with an additional rotation key letter</li>
 * </ul>
 *
 * <p>All methods operate on uppercase letters. Non-alphabetic characters (except spaces,
 * which are preserved as-is) are passed through unchanged.</p>
 */
public class Cipher {

    /** The standard English uppercase alphabet used as the base for all cipher operations. */
    static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Encrypts text using a simple columnar transposition cipher.
     *
     * <p>Spaces are removed, then characters at even indices form the first half
     * and characters at odd indices form the second half. Both halves are concatenated.</p>
     *
     * <p>Example: {@code "HELLO"} → {@code "HLELO"}</p>
     *
     * @param text the plaintext to encrypt (spaces will be stripped)
     * @return the transposition-ciphered uppercase string
     */
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

    /**
     * Encrypts text using a Giovanni cipher, which is a keyword cipher with an additional
     * rotational shift determined by a key letter.
     *
     * <p>The cipher alphabet is built from the keyword (deduplicated, uppercase) followed
     * by the remaining letters of the standard alphabet. The resulting alphabet is then
     * rotated so that it starts at the position corresponding to {@code keyLetter}.</p>
     *
     * <p>Spaces are preserved; non-alphabetic, non-space characters are passed through unchanged.</p>
     *
     * @param text      the plaintext to encrypt
     * @param keyword   the keyword used to build the cipher alphabet (duplicates are removed)
     * @param keyLetter a single letter determining the rotational offset of the cipher alphabet
     * @return the Giovanni-ciphered uppercase string
     */
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

    /**
     * Encrypts text using a keyword substitution cipher.
     *
     * <p>A cipher alphabet is built by prepending the keyword (with duplicates removed)
     * to the remaining letters of the standard alphabet. Each plaintext letter is then
     * replaced by the corresponding letter in this cipher alphabet.</p>
     *
     * <p>Spaces are preserved; non-alphabetic, non-space characters are passed through unchanged.</p>
     *
     * <p>Example: keyword {@code "KEY"} produces cipher alphabet {@code "KEYABCDFGHIJLMNOPQRSTUVWXZ"},
     * so {@code 'A'} maps to {@code 'K'}, {@code 'B'} maps to {@code 'E'}, etc.</p>
     *
     * @param text    the plaintext to encrypt
     * @param keyword the keyword used to build the substitution alphabet (duplicates are removed)
     * @return the keyword-ciphered uppercase string
     */
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

    /**
     * Encrypts text using a Caesar cipher with a given shift value.
     *
     * <p>Each alphabetic character is replaced by the letter {@code shift} positions later
     * in the alphabet, wrapping around from Z back to A. Spaces are preserved;
     * non-alphabetic, non-space characters are passed through unchanged.</p>
     *
     * <p>Example: {@code "ABC"} with shift {@code 3} → {@code "DEF"}</p>
     *
     * @param text  the plaintext to encrypt
     * @param shift the number of positions to shift each letter (0–25)
     * @return the Caesar-ciphered uppercase string
     */
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