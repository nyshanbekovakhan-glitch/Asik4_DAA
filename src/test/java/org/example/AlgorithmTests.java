package org.example;
import org.example.dp.EditDistance;
import org.example.dp.LCS;
import org.example.matching.KMP;
import org.example.matching.NaiveSearch;
import org.example.matching.RabinKarp;
import org.example.suffix.Kasai;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTests {
    @Test
    void lcsCases() {
        assertEquals(3, LCS.findLength("abcde", "ace"));
        assertEquals(4, LCS.findLength("same", "same"));
        assertEquals(0, LCS.findLength("abc", "xyz"));
        assertEquals(0, LCS.findLength("", "abc"));
        assertEquals(0, LCS.findLength("", ""));
    }
    @Test
    void editDistanceCases() {
        assertEquals(0, EditDistance.calculate("abc", "abc"));
        assertEquals(3, EditDistance.calculate("", "abc"));
        assertEquals(3, EditDistance.calculate("abc", ""));
        assertEquals(1, EditDistance.calculate("cat", "cut"));
        assertEquals(1, EditDistance.calculate("cat", "cats"));
        assertEquals(1, EditDistance.calculate("cats", "cat"));
        assertEquals(3, EditDistance.calculate("kitten", "sitting"));
    }
    @Test
    void stringMatchingCases() {
        checkAll("abcde", "ab", List.of(0));
        checkAll("xxabcxx", "abc", List.of(2));
        checkAll("xxabc", "abc", List.of(2));
        checkAll("abcabcabc", "abc", List.of(0, 3, 6));
        checkAll("abcdef", "xyz", List.of());
        checkAll("abc", "abcd", List.of());
        checkAll("abc", "", List.of(0, 1, 2, 3));
        checkAll("", "", List.of(0));
        checkAll("aaaaa", "aaa", List.of(0, 1, 2));
    }
    private void checkAll(
            String text,
            String pattern,
            List<Integer> expected) {
        assertEquals(expected, NaiveSearch.search(text, pattern));
        assertEquals(expected, KMP.search(text, pattern));
        assertEquals(expected, RabinKarp.search(text, pattern));
    }
    @Test
    void kmpLpsTable() {
        assertArrayEquals(
                new int[]{0, 0, 1, 2, 3, 0},
                KMP.buildLps("ababac")
        );
    }
    @Test
    void kasaiKnownExample() {
        String text = "banana";
        int[] suffixArray = {5, 3, 1, 0, 4, 2};
        assertArrayEquals(
                new int[]{0, 1, 3, 0, 0, 2},
                Kasai.buildLcp(text, suffixArray)
        );
    }
    @Test
    void kasaiRejectsInvalidSuffixArray() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Kasai.buildLcp("abc", new int[]{0, 1, 1})
        );
    }
}
