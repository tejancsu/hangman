package strategy;

import junit.framework.TestCase;
import lib.ProbabilityHelper;
import lib.dictionary.DictionaryClient;
import model.MyGuess;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
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

    @Test
    public void testComputeNextGuess() {
        buildTestMyGameStrategy();
        List<String> phraseWords = Arrays.asList("_t", "___d");
        List<Character> previousGuesses = Arrays.asList('a');
        MyGuess myGuess = new MyGuess('e', 0.125F);
        when(myGameStrategy.getPhraseWords(anyString())).thenReturn(phraseWords);
        when(myGameStrategy.pruneDictionary(anyMap(), anyList())).thenReturn(myGameStrategy.dictionaryMap);
        when(myGameStrategy.tryAllCharacters(anyList(), anyMap(), anyList())).thenReturn(myGuess);
        assertTrue(myGameStrategy.computeNextGuess("phrase", previousGuesses, 5).get().equals('e'));
        verify(myGameStrategy, times(1)).getPhraseWords("phrase");
        verify(myGameStrategy, times(1)).pruneDictionary(myGameStrategy.dictionaryMap, phraseWords);
        verify(myGameStrategy, times(1)).tryAllCharacters(phraseWords, myGameStrategy.dictionaryMap, previousGuesses);
    }

    @Test
    public void testComputeNextGuessLastTry() {
        buildTestMyGameStrategy();
        List<String> phraseWords = Arrays.asList("_t", "___d");
        List<Character> previousGuesses = Arrays.asList('a');
        MyGuess myGuess = new MyGuess('e', 0.125F);
        when(myGameStrategy.getPhraseWords(anyString())).thenReturn(phraseWords);
        when(myGameStrategy.pruneDictionary(anyMap(), anyList())).thenReturn(myGameStrategy.dictionaryMap);
        MyGuess myGuess2 = new MyGuess('f', 0.25F);
        when(myGameStrategy.tryAllCharacters(anyList(), anyMap(), anyList())).thenReturn(myGuess).thenReturn(myGuess2);
        assertTrue(myGameStrategy.computeNextGuess("phrase", previousGuesses, 0).get().equals('f'));

        // If guess using mfwDictionary is bad use default
        myGuess2 = new MyGuess('f', 0.01F);
        when(myGameStrategy.tryAllCharacters(anyList(), anyMap(), anyList())).thenReturn(myGuess).thenReturn(myGuess2);
        assertTrue(myGameStrategy.computeNextGuess("phrase", previousGuesses, 0).get().equals('e'));
    }


    @Test
    public void testUpdateStrategy() throws Exception {
        buildTestMyGameStrategy();
        // hit will not change the dictionary
        myGameStrategy.updateStrategy('b', true);
        assertTrue(myGameStrategy.dictionaryMap.get(4).contains("band"));

        //miss changes the dictionary
        myGameStrategy.updateStrategy('b', false);
        assertEquals(myGameStrategy.dictionaryMap.get(2), new HashSet<>(Arrays.asList("at", "it", "ht")));
        assertEquals(myGameStrategy.dictionaryMap.get(4), new HashSet<>(Arrays.asList("land", "aand", "hand")));

        myGameStrategy.updateStrategy('h', false);
        assertEquals(myGameStrategy.dictionaryMap.get(2), new HashSet<>(Arrays.asList("at", "it")));
        assertEquals(myGameStrategy.dictionaryMap.get(4), new HashSet<>(Arrays.asList("land", "aand")));
    }

    private void buildTestMyGameStrategy() {
        DictionaryClient allWordsDictionaryClient;
        DictionaryClient mfuWordsDictionaryClient;
        allWordsDictionaryClient = mock(DictionaryClient.class);
        mfuWordsDictionaryClient = mock(DictionaryClient.class);
        List<String> allWordsMock = Arrays.asList("at", "it", "ht",
                "land", "hand", "band", "aand");
        List<String> mfuWordsMock = Arrays.asList("it", "hand", "band");
        when(allWordsDictionaryClient.getAllWords()).thenReturn(allWordsMock);
        when(mfuWordsDictionaryClient.getAllWords()).thenReturn(mfuWordsMock);
        myGameStrategy = spy(new MyGameStrategy(allWordsDictionaryClient, mfuWordsDictionaryClient));
    }

    @Test
    public void testTryAllCharacters() throws Exception {
        List<String> phraseWords = new ArrayList<>();
        Map<Integer, Set<String>> dictionaryMap = new HashMap<>();
        List<Character> previousGuesses = Arrays.asList('a', 'e', 's');
        when(myGameStrategy.getProbabilityOfHit(anyChar(), anyMap(), anyList())).thenReturn(0.25F);
        when(myGameStrategy.getProbabilityOfHit('c', dictionaryMap, phraseWords)).thenReturn(0.5F);
        MyGuess guess = myGameStrategy.tryAllCharacters(phraseWords, dictionaryMap, previousGuesses);
        assertTrue(guess.getGuess().equals('c'));
        assertEquals(guess.getProbability(), 0.5F);
        for (char c = 'a'; c <= 'z'; c++) {
            if(previousGuesses.contains(c)) {
                verify(myGameStrategy, times(0)).getProbabilityOfHit(c, dictionaryMap, phraseWords);
            } else {
                verify(myGameStrategy, times(1)).getProbabilityOfHit(c, dictionaryMap, phraseWords);
            }
        }
    }

    @Test
    public void testGetProbabilityOfHit() {
        Map<Integer, Set<String>> dictionaryMap = new HashMap<>();
        Set<String> set2 = new HashSet<>(Arrays.asList("at", "it"));
        Set<String> set4 = new HashSet<>(Arrays.asList("land", "hand", "band", "aand"));
        dictionaryMap.put(2, set2);
        dictionaryMap.put(4, set4);
        // Only "_i" to match
        List<String> phraseWords = Arrays.asList("_t", "_and");
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set2, "_t")).thenReturn(set2);
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set4, "_and")).thenReturn(Collections.<String>emptySet());
        assertEquals(myGameStrategy.getProbabilityOfHit('a', dictionaryMap, phraseWords), 0.5F);

        // Both "_i" and "_and" to match
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set4, "_and")).thenReturn(set4);
        assertEquals(myGameStrategy.getProbabilityOfHit('a', dictionaryMap, phraseWords), ProbabilityHelper.getCummulativeProbabilityOfIndependantEvents(0.25F, 0.5F));

        //No dictionary words to match up against
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set2, "_t")).thenReturn(Collections.<String>emptySet());
        when(myGameStrategy.filterDictionaryWordsMatchingPhraseWord(set4, "_and")).thenReturn(Collections.<String>emptySet());
        assertEquals(myGameStrategy.getProbabilityOfHit('a', dictionaryMap, phraseWords), 0F);
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