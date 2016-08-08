package lib.dictionary;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Most frquently used words dictionary
 */
public class MostFrequentlyUsedWordsDictionaryClient extends WebDictionaryClient {

    @Override
    protected URL dictionaryEndPoint() {
        try {
            return new URL("https://gist.githubusercontent.com/deekayen/4148741/raw/01c6252ccc5b5fb307c1bb899c95989a8a284616/1-1000.txt");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}