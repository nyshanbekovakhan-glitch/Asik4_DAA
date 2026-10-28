package org.example;
import org.example.dp.EditDistance;
import org.example.dp.LCS;
import org.example.experiment.ExperimentRunner;
import org.example.matching.KMP;
import org.example.matching.NaiveSearch;
import org.example.matching.RabinKarp;
import org.example.suffix.Kasai;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("LCS: " + LCS.findLength("abcde", "ace"));
        System.out.println("Edit Distance: " + EditDistance.calculate("cat", "cut"));
        String text = "ababcabcab";
        String pattern = "abc";
        System.out.println("Naive: " + NaiveSearch.search(text, pattern));
        System.out.println("KMP: " + KMP.search(text, pattern));
        System.out.println("Rabin-Karp: " + RabinKarp.search(text, pattern));
        String sample = "banana";
        int[] suffixArray = {5, 3, 1, 0, 4, 2};
        System.out.println("Kasai LCP: " + Arrays.toString(
                        Kasai.buildLcp(sample, suffixArray)
                )
        );

        if (args.length > 0
                && args[0].equalsIgnoreCase("experiment")) {
            ExperimentRunner.runAll();
        }
    }
}
