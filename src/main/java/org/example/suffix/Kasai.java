package org.example.suffix;
public class Kasai {
    public static int[] buildLcp(String text, int[] suffixArray) {
        int n = text.length();
        if (suffixArray.length != n) {
            throw new IllegalArgumentException(
                    "Suffix array length must equal string length."
            );
        }
        boolean[] seen = new boolean[n];
        for (int suffix : suffixArray) {
            if (suffix < 0 || suffix >= n || seen[suffix]) {
                throw new IllegalArgumentException(
                        "Suffix array must be a permutation of 0..n-1."
                );
            }
            seen[suffix] = true;
        }
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }
        int[] lcp = new int[n];
        int common = 0;
        for (int i = 0; i < n; i++) {
            int r = rank[i];
            if (r == 0) {
                common = 0;
                continue;
            }
            int j = suffixArray[r - 1];
            while (i + common < n
                    && j + common < n
                    && text.charAt(i + common)
                    == text.charAt(j + common)) {
                common++;
            }
            lcp[r] = common;
            if (common > 0) {
                common--;
            }
        }
        return lcp;
    }
}
