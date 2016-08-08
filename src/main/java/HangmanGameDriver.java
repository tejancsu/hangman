import lib.dictionary.DictionaryClient;
import lib.dictionary.MostFrequentlyUsedWordsDictionaryClient;
import lib.dictionary.SilWebDictionaryClient;
import strategy.HangmanGameStrategy;
import strategy.MyGameStrategy;

/**
 * Game driver for multiple runs
 */
public class HangmanGameDriver {
    private int runs;

    public HangmanGameDriver(int runs) {
        this.runs = runs;
    }

    public void start() {
        int numWins = 0;
        for(int i = 0; i < runs; i++) {
            DictionaryClient dictionaryClient = new SilWebDictionaryClient();
            DictionaryClient mfuDictionaryClient = new MostFrequentlyUsedWordsDictionaryClient();
            HangmanGameStrategy gameStrategy = new MyGameStrategy(dictionaryClient, mfuDictionaryClient);
            HangmanGame hangmanGame = new HangmanGame(gameStrategy);
            if(hangmanGame.run())
                numWins += 1;
        }
        System.out.println("--------------------------");
        System.out.println("Total number of runs:" + runs);
        System.out.println("Total wins:" + numWins);
        System.out.println("Win percentage:" + numWins*100/runs);
    }

    public static void main(String[] args) {
        for(String arg : args)
            System.out.println(arg);
        if(args.length != 2) {
            throw new RuntimeException("Usage: HangmanGameDriver <numRuns>");
        }
        int numRuns = new Integer(args[1]);
        HangmanGameDriver gameDriver = new HangmanGameDriver(numRuns);
        gameDriver.start();
    }
}
