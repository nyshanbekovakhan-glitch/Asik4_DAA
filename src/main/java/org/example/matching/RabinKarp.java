package org.example.matching;
import java.util.ArrayList;
import java.util.List;
public class RabinKarp {
    private static final long BASE = 256;
    private static final long MOD = 1_000_000_007L;
    public static List<Integer> search(String text, String pattern) {
        return search(text, pattern, null);
    }
    public static List<Integer> search(
            String text, String pattern, long[] verifications) {
        List<Integer> matches = new ArrayList<>();
        if (pattern.isEmpty()) {
            for (int i = 0; i <= text.length(); i++) {
                matches.add(i);
            }
            return matches;
        }
        int n = text.length();
        int m = pattern.length();
        if (m > n) {
            return matches;
        }
        long highPower = 1;
        for (int i = 1; i < m; i++) {
            highPower = (highPower * BASE) % MOD;
        }
        long patternHash = 0;
        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            patternHash =
                    (patternHash * BASE + pattern.charAt(i)) % MOD;
            windowHash =
                    (windowHash * BASE + text.charAt(i)) % MOD;
        }
        for (int start = 0; start <= n - m; start++) {
            if (patternHash == windowHash) {
                boolean equal = true;
                for (int j = 0; j < m; j++) {
                    if (verifications != null) {
                        verifications[0]++;
                    }
                    if (text.charAt(start + j) != pattern.charAt(j)) {
                        equal = false;
                        break;
                    }
                }
                if (equal) {
                    matches.add(start);
                }
            }
            if (start < n - m) {
                long leading =
                        (text.charAt(start) * highPower) % MOD;
                windowHash =
                        (windowHash - leading + MOD) % MOD;

                windowHash =
                        (windowHash * BASE + text.charAt(start + m)) % MOD;
            }
        }
        return matches;
    }
}
