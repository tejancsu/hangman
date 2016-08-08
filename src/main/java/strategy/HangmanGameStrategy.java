package strategy;

import java.util.List;
import java.util.Optional;

/**
 * Interface for any hangman game driver
 */
public interface HangmanGameStrategy {
    Optional<Character> computeNextGuess(String phrase, List<Character> previousGuesses, int numTriesLeft);
    void updateStrategy(char guess, boolean isHit);
}
