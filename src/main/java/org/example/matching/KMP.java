package org.example.matching;
import java.util.ArrayList;
import java.util.List;
public class KMP {
    public static List<Integer> search(String text, String pattern) {
        return search(text, pattern, null);
    }
    public static List<Integer> search(
            String text, String pattern, long[] comparisons) {
        List<Integer> matches = new ArrayList<>();
        if (pattern.isEmpty()) {
            for (int i = 0; i <= text.length(); i++) {
                matches.add(i);
            }
            return matches;
        }
        if (pattern.length() > text.length()) {
            return matches;
        }
        int[] lps = buildLps(pattern);
        int i = 0;
        int j = 0;
        while (i < text.length()) {
            if (comparisons != null) {
                comparisons[0]++;
            }
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return matches;
    }
    public static int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }
}
