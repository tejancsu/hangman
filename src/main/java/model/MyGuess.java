package model;

/**
 * Model for a Guess
 */
public class MyGuess {
    private Character guess;
    private Float probability;

    public MyGuess(Character guess, Float probability) {
        this.guess = guess;
        this.probability = probability;
    }

    public Character getGuess() {
        return guess;
    }

    public void setGuess(Character guess) {
        this.guess = guess;
    }

    public Float getProbability() {
        return probability;
    }

    public void setProbability(Float probability) {
        this.probability = probability;
    }
}
