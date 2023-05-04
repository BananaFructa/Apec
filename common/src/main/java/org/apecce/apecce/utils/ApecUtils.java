package org.apecce.apecce.utils;

public class ApecUtils {

    public static boolean isContainedIn(String s1, String s2) {
        char[] targetChars = s2.toCharArray();
        char[] sourceChars = s1.toCharArray();

        int targetIndex = 0;
        for (char sourceChar : sourceChars) {
            if (sourceChar == targetChars[targetIndex]) {
                targetIndex++;
                if (targetIndex == targetChars.length) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean containedByCharSequence(String str, String substr) {
        if (substr.length() > str.length()) {
            return false;
        }
        int j = 0;
        for (char c : str.toCharArray()) {
            if (c == substr.charAt(j)) {
                j++;
                if (j == substr.length()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String removeFirstSpaces(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        int i = 0;
        while (i < s.length() && s.charAt(i) == ' ') {
            i++;
        }

        return s.substring(i);
    }

}
