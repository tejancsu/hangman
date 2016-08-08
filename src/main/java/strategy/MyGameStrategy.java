package strategy;

import lib.dictionary.DictionaryClient;
import lib.ProbabilityHelper;
import model.MyGuess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Game strategy based on probability of hit
 */
public class MyGameStrategy implements HangmanGameStrategy {
    protected Map<Integer, Set<String>> dictionaryMap;
    protected Map<Integer, Set<String>> mostFrequentlyUsedDictoryMap;

    public MyGameStrategy(DictionaryClient allWordDictionaryClient, DictionaryClient mostFrequentlyUsedDictionaryClient) {
        this.dictionaryMap = buildDictionaryMap(allWordDictionaryClient.getAllWords());
        this.mostFrequentlyUsedDictoryMap = buildDictionaryMap(mostFrequentlyUsedDictionaryClient.getAllWords());
    }

    /**
     * Computes next character guess
     * @param phrase current state of the phrase that needs to be guessed
     * @param previousGuesses characters guessed so far
     * @param numTriesLeft number of tries left in the game
     * @return Optional<Character> next guess
     */
    @Override
    public Optional<Character> computeNextGuess(String phrase, List<Character> previousGuesses, int numTriesLeft) {
        List<String> phraseWords = getPhraseWords(phrase);
        dictionaryMap = pruneDictionary(dictionaryMap, phraseWords);
        MyGuess nextGuess = tryAllCharacters(phraseWords, dictionaryMap, previousGuesses);

        //Last minute push - use most-frequently-used words
        if((numTriesLeft < 1) && (nextGuess.getProbability() < 0.5))
            if(phraseWords.stream().filter(w -> (w.length() < 5)).count() >= 1) {
                mostFrequentlyUsedDictoryMap = pruneDictionary(mostFrequentlyUsedDictoryMap, phraseWords);
                MyGuess mfwNextGuess = tryAllCharacters(phraseWords, mostFrequentlyUsedDictoryMap, previousGuesses);
                if((mfwNextGuess != null) && (mfwNextGuess.getProbability() > nextGuess.getProbability()))
                    nextGuess = mfwNextGuess;
            }

        return Optional.ofNullable(nextGuess.getGuess());
    }

    /**
     * Update dictionary after guessed character is wrong
     * @param guess Character guessed in the previous guess round
     * @param isHit if result is a hit
     */
    @Override
    public void updateStrategy(final char guess, boolean isHit) {
        if(!isHit) {
            Map<Integer, Set<String>> newDictionaryMap = new HashMap<>();
            dictionaryMap.forEach((k, wordSet) -> {
                Set newSet = wordSet.stream()
                                .filter(word -> (word.indexOf(guess) < 0))
                                .collect(Collectors.toSet());
                newDictionaryMap.put(k, newSet);
            });
            dictionaryMap = newDictionaryMap;
        }
    }

    protected MyGuess tryAllCharacters(List<String> phraseWords, Map<Integer, Set<String>> dictionaryMap,
                                       List<Character> previousGuesses) {
        float max_probability = -1;
        float cur_probability;
        Character nextGuess = null;
        for (char c = 'a'; c <= 'z'; c++) {
            if(!previousGuesses.contains(c)) {
                cur_probability = getProbabilityOfHit(c, dictionaryMap, phraseWords);
                if(cur_probability > max_probability) {
                    max_probability = cur_probability;
                    nextGuess = c;
                }
            }
        }

        return new MyGuess(nextGuess, max_probability);
    }

    protected Float getProbabilityOfHit(char letter, Map<Integer, Set<String>> dictionaryMap, List<String> phraseWords) {
        List<Float> probabilities = new ArrayList<>();
        for(final String phraseWord : phraseWords) {
            final int phraseWordLen = phraseWord.length();
            Set<String> relevantDictionaryWords = filterDictionaryWordsMatchingPhraseWord(
                    dictionaryMap.get(phraseWordLen), phraseWord);
            if(!relevantDictionaryWords.isEmpty()) {
                Long potentialMatchesCount = relevantDictionaryWords.stream().filter(dictionaryWord -> {
                    for (int i = 0; i < phraseWordLen; i++) {
                        if ((phraseWord.charAt(i) == '_') && (dictionaryWord.charAt(i) == letter))
                            return true;
                    }
                    return false;
                }).count();
                probabilities.add(potentialMatchesCount / (float) relevantDictionaryWords.size());
            }
        }
        return ProbabilityHelper.getCummulativeProbabilityOfIndependantEvents(probabilities);
    }

    protected Map<Integer, Set<String>> buildDictionaryMap(List<String> dictionaryList) {
        Map<Integer, Set<String>> dictionaryMap = new HashMap<>();
        for(String word : dictionaryList) {
            int len = word.length();
            if(!dictionaryMap.containsKey(len)) {
                dictionaryMap.put(len, new HashSet<String>());
            }
            Set<String> wordBucket = dictionaryMap.get(len);
            wordBucket.add(word);
        }
        return dictionaryMap;
    }

    protected Map<Integer, Set<String>> pruneDictionary(Map<Integer, Set<String>> dictionaryMap,
                                                        List<String> phraseWords) {
        Map<Integer, Set<String>> newDictionaryMap = new HashMap<>();
        phraseWords.forEach(phraseWord -> {
            int wordLen = phraseWord.length();
            if(dictionaryMap.containsKey(wordLen)) {
                Set<String> bucket = filterDictionaryWordsMatchingPhraseWord(dictionaryMap.get(wordLen), phraseWord);
                if(!newDictionaryMap.containsKey(wordLen))
                    newDictionaryMap.put(wordLen, new HashSet<>());
                newDictionaryMap.get(wordLen).addAll(bucket);
            }
        });

        return newDictionaryMap;
    }

    protected Set<String> filterDictionaryWordsMatchingPhraseWord(Set<String> dictionaryWords, String phraseWord) {
        return dictionaryWords.stream().filter(dictionaryWord -> {
                    if(phraseWord.equals(dictionaryWord))
                        return false;

                    boolean shouldFilter = true;
                    for (int i = 0; i < phraseWord.length(); i++) {
                        if (phraseWord.charAt(i) != '_') {
                            shouldFilter = shouldFilter && (phraseWord.charAt(i) == dictionaryWord.charAt(i));
                        }
                    }
                    return shouldFilter;
                }).collect(Collectors.toSet());
    }

    protected List<String> getPhraseWords(String phrase) {
        List<String> phraseWords = Arrays.asList(phrase.split(" "));
        //return only non resolved phrase words
        return phraseWords.stream().filter(phraseWord -> (phraseWord.indexOf('_') >= 0)).collect(Collectors.toList());
    }
}
