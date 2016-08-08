package strategy;

import junit.framework.TestCase;
import lib.ProbabilityHelper;
import lib.dictionary.DictionaryClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for MyGameStrategy
 */
public class MyGameStrategyTest extends TestCase {

    private MyGameStrategy myGameStrategy;

    @Before
    public void setUp() {
        myGameStrategy = spy(new MyGameStrategy(mock(DictionaryClient.class), mock(DictionaryClient.class)));
    }

    public void testComputeNextGuess() throws Exception {

    }

    public void testUpdateStrategy() throws Exception {

    }

    @Test
    public void testGetProbabilityOfHit() {
        Map<Integer, Set<String>> dictionaryMap = new HashMap<>();
        Set<String> set2 = new HashSet<>(Arrays.asList("at", "it"));
        Set<String> set4 = new HashSet<>(Arrays.asList("land", "hand", "band", "aand"));
        dictionaryMap.put(2, set2);
        dictionaryMap.put(4, set4);
        List<String> phraseWords = Arrays.asList("_t", "_and");
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set2, "_t")).thenReturn(set2);
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set4, "_and")).thenReturn(Collections.<String>emptySet());
        assertEquals(myGameStrategy.getProbabilityOfHit('a', dictionaryMap, phraseWords), 0.5F);

        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set4, "_and")).thenReturn(set4);
        assertEquals(myGameStrategy.getProbabilityOfHit('a', dictionaryMap, phraseWords), ProbabilityHelper.getCummulativeProbabilityOfIndependantEvents(0.25F, 0.5F));
    }

    @Test
    public void testBuildDictionaryMap() {
        Map<Integer, Set<String>> dictionaryMap = myGameStrategy.buildDictionaryMap(
                Arrays.asList("a", "a1", "a2", "a12", "a22"));
        assertEquals(dictionaryMap.get(1).size(), 1);
        assertEquals(dictionaryMap.get(2).size(), 2);
        assertEquals(dictionaryMap.get(3).size(), 2);
        assertEquals(dictionaryMap.get(3), new HashSet<>(Arrays.asList("a22", "a12")));
    }

    @Test
    public void testPruneDictionary() {
        Map<Integer, Set<String>> dictionaryMap = new HashMap<>();
        Set<String> set1 = new HashSet<>(Arrays.asList("a11", "a12"));
        Set<String> set2 = new HashSet<>(Arrays.asList("a21", "a22"));
        Set<String> set3 = new HashSet<>(Arrays.asList("a31", "a32", "a33"));
        Set<String> set4 = new HashSet<>(Arrays.asList("a41", "a42", "a43", "a44"));
        dictionaryMap.put(1, set1);
        dictionaryMap.put(2, set2);
        dictionaryMap.put(3, set3);
        dictionaryMap.put(4, set4);
        List<String> phraseWords = Arrays.asList("p1", "p123", "p2");
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set2, "p1")).thenReturn(set2);
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set4, "p123")).thenReturn(set4);
        dictionaryMap = myGameStrategy.pruneDictionary(dictionaryMap, phraseWords);
        assertEquals(dictionaryMap.size(), 2);
        assertEquals(dictionaryMap.get(2), set2);
        verify(myGameStrategy, times(1)).filterDictionaryWordsMatchingPhraseWord(set2, "p1");
        verify(myGameStrategy, times(1)).filterDictionaryWordsMatchingPhraseWord(set2, "p2");
        verify(myGameStrategy, times(1)).filterDictionaryWordsMatchingPhraseWord(set4, "p123");
        verify(myGameStrategy, times(1)).filterDictionaryWordsMatchingPhraseWord(set4, "p123");

    }

    @Test
    public void testFilterDictionaryWordsMatchingPhraseWord() {
        Set<String> dictionary = new HashSet<>(Arrays.asList("hat", "bat", "lit"));
        assertEquals(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(dictionary, "_at"), new HashSet<>(Arrays.asList("hat", "bat")));
        assertTrue(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(dictionary, "_et").isEmpty());
        assertTrue(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(dictionary, "bat").isEmpty());
    }

    @Test
    public void testGetPhraseWords() throws Exception {
        String phrase;
        phrase = "_____ ne_ y___";
        assertEquals(myGameStrategy.getPhraseWords(phrase), Arrays.asList("_____", "ne_", "y___"));
        phrase = "h____ new y_ar";
        assertEquals(myGameStrategy.getPhraseWords(phrase), Arrays.asList("h____", "y_ar"));
    }

}