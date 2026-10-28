package org.example.experiment;
import org.example.dp.EditDistance;
import org.example.dp.LCS;
import org.example.matching.KMP;
import org.example.matching.NaiveSearch;
import org.example.matching.RabinKarp;
import org.example.suffix.Kasai;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
public class ExperimentRunner {
    private static final long SEED = 42L;
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int REPETITIONS = 5;
    public static void runAll() throws IOException {
        Files.createDirectories(Path.of("data"));
        List<String> rows = new ArrayList<>();
        rows.add("category,algorithm,input_size,average_time_ns,operation_count,scenario");
        runDpExperiments(rows);
        runMatchingExperiments(rows);
        runKasaiExperiments(rows);
        Files.write(Path.of("data/results.csv"), rows);
        System.out.println("Results saved to data/results.csv");
    }
    private static void runDpExperiments(List<String> rows) {
        int[] sizes = {100, 500, 1000};
        Random random = new Random(SEED);
        for (int size : sizes) {
            String a = randomString(random, size);
            String b = randomString(random, size);
            long lcsTime = averageTime(
                    () -> LCS.findLength(a, b)
            );
            rows.add(csv("DP", "LCS", size, lcsTime,
                    (long) size * size, "random"
            ));
            long editTime = averageTime(
                    () -> EditDistance.calculate(a, b)
            );
            rows.add(csv("DP", "EditDistance", size, editTime,
                    (long) size * size, "random"
            ));
            System.out.printf("DP n=%d: LCS=%d ns, EditDistance=%d ns%n",
                    size, lcsTime, editTime
            );
        }
    }
    private static void runMatchingExperiments(
            List<String> rows) {
        int[] sizes = {10_000, 50_000, 100_000};
        Random random = new Random(SEED + 1);
        for (int size : sizes) {
            String text = randomString(random, size);
            int patternLength = 20;
            String present = text.substring(
                    size / 2, size / 2 + patternLength
            );
            String absent = "zzzzzzzzzzzzzzzzzzzy";
            runOneMatchingSize(
                    rows, text, present, size, "present"
            );
            runOneMatchingSize(
                    rows, text, absent, size, "absent"
            );
        }
    }
    private static void runOneMatchingSize(
            List<String> rows,
            String text,
            String pattern,
            int size,
            String scenario) {
        long[] naiveOps = {0};
        long naiveTime = averageTime(() -> {
            naiveOps[0] = 0;
            NaiveSearch.search(text, pattern, naiveOps);
        });
        rows.add(csv(
                "Matching", "Naive", size,
                naiveTime, naiveOps[0], scenario
        ));
        long[] kmpOps = {0};
        long kmpTime = averageTime(() -> {
            kmpOps[0] = 0;
            KMP.search(text, pattern, kmpOps);
        });
        rows.add(csv(
                "Matching", "KMP", size,
                kmpTime, kmpOps[0], scenario
        ));
        long[] rkOps = {0};
        long rkTime = averageTime(() -> {
            rkOps[0] = 0;
            RabinKarp.search(text, pattern, rkOps);
        });
        rows.add(csv(
                "Matching", "RabinKarp", size,
                rkTime, rkOps[0], scenario
        ));
        System.out.printf(
                "Matching n=%d (%s): Naive=%d ns, KMP=%d ns, RabinKarp=%d ns%n",
                size, scenario, naiveTime, kmpTime, rkTime
        );
    }
    private static void runKasaiExperiments(
            List<String> rows) {
        int[] sizes = {1000, 5000, 10000};
        Random random = new Random(SEED + 2);
        for (int size : sizes) {
            String text = randomString(random, size);
            int[] suffixArray = buildSuffixArray(text);
            long time = averageTime(
                    () -> Kasai.buildLcp(text, suffixArray)
            );
            rows.add(csv(
                    "Suffix", "Kasai", size,
                    time, size, "random"
            ));
            System.out.printf(
                    "Kasai n=%d: %d ns%n", size, time
            );
        }
    }
    private static int[] buildSuffixArray(String text) {
        int n = text.length();
        Integer[] order = new Integer[n];
        int[] rank = new int[n];
        int[] nextRank = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
            rank[i] = text.charAt(i);
        }
        for (int length = 1; length < n; length *= 2) {
            final int currentLength = length;
            final int[] currentRank = rank;
            Arrays.sort(order, (a, b) -> {
                if (currentRank[a] != currentRank[b]) {
                    return Integer.compare(currentRank[a], currentRank[b]);
                }
                int rankA = a + currentLength < n
                        ? currentRank[a + currentLength] : -1;
                int rankB = b + currentLength < n
                        ? currentRank[b + currentLength] : -1;
                return Integer.compare(rankA, rankB);
            });
            nextRank[order[0]] = 0;
            for (int i = 1; i < n; i++) {
                int previous = order[i - 1];
                int current = order[i];
                boolean different =
                        rank[previous] != rank[current];
                int previousSecond =
                        previous + currentLength < n
                                ? rank[previous + currentLength] : -1;
                int currentSecond =
                        current + currentLength < n
                                ? rank[current + currentLength] : -1;
                if (different
                        || previousSecond != currentSecond) {
                    nextRank[current] = nextRank[previous] + 1;
                } else {
                    nextRank[current] = nextRank[previous];
                }
            }
            int[] temp = rank;
            rank = nextRank;
            nextRank = temp;
            if (rank[order[n - 1]] == n - 1) {
                break;
            }
            if (length > n / 2) {
                break;
            }
        }
        int[] suffixArray = new int[n];
        for (int i = 0; i < n; i++) {
            suffixArray[i] = order[i];
        }
        return suffixArray;
    }
    private static long averageTime(Runnable action) {
        for (int i = 0; i < 2; i++) {
            action.run();
        }
        long total = 0;
        for (int i = 0; i < REPETITIONS; i++) {
            long start = System.nanoTime();
            action.run();
            total += System.nanoTime() - start;
        }
        return total / REPETITIONS;
    }
    private static String randomString(
            Random random, int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(
                    ALPHABET[random.nextInt(ALPHABET.length)]
            );
        }
        return result.toString();
    }
    private static String csv(
            String category,
            String algorithm,
            int size,
            long time,
            long operations,
            String scenario) {
        return category + "," + algorithm + "," + size + ","
                + time + "," + operations + "," + scenario;
    }
}
