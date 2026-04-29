package sample_codebase;

/**
 * Sample Java file for evolution simulation testing.
 * This file represents a simple utility class.
 */
public class StringUtils {
    
    public static String reverse(String input) {
        return new StringBuilder(input).reverse().toString();
    }
    
    public static boolean isPalindrome(String word) {
        return word.equals(reverse(word));
    }
}
