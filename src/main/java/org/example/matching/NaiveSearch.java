package org.example.matching;
import java.util.ArrayList;
import java.util.List;
public class NaiveSearch {
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
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length()) {
                if (comparisons != null) {
                    comparisons[0]++;
                }if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }
            if (j == pattern.length()) {
                matches.add(i);
            }
        }
        return matches;
    }
}
