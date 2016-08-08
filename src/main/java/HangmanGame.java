import api.Api;
import strategy.HangmanGameStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * One hangman game session
 */
class HangmanGame {

    private final HangmanGameStrategy hangmanGameStrategy;
    private final String gameKey;
    private String phrase;
    private int numRetriesLeft;
    private List<Character> guesses;

    public HangmanGame(HangmanGameStrategy gameStrategy) {
        this.hangmanGameStrategy = gameStrategy;
        Api.GameResponse response = Api.sendNewGameRequest("test@test.com");
        this.gameKey = response.game_key;
        this.phrase = response.phrase;
        this.numRetriesLeft = response.num_tries_left;
        this.guesses = new ArrayList<>();
    }

    public boolean run() {
        System.out.println("Game " + gameKey + " starts");
        Api.GameResponse response;
        boolean game_won = false;
        boolean alive = true;
        while(alive && !game_won) {
            Optional<Character> nextGuess = hangmanGameStrategy.computeNextGuess(phrase, guesses, numRetriesLeft);
            if (nextGuess.isPresent()) {
                response = Api.sendGuessRequest(gameKey, nextGuess.get());
                System.out.println(nextGuess.get());
                System.out.println(response);
                boolean isHit = (response.num_tries_left == numRetriesLeft);
                hangmanGameStrategy.updateStrategy(nextGuess.get(), isHit);
                numRetriesLeft = response.num_tries_left;
                guesses.add(nextGuess.get());
                phrase = response.phrase;
                game_won = (response.state.equals("won"));
                alive = (response.state.equals("alive"));
            } else {
                throw new RuntimeException("Game strategy couldnt guess next character");
            }
        }
        return game_won;
    }
}