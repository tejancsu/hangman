package lib.dictionary;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * All words dictionary
 */
public class SilWebDictionaryClient extends WebDictionaryClient {

    @Override
    protected URL dictionaryEndPoint() {
        try {
            return new URL("http://www-01.sil.org/linguistics/wordlists/english/wordlist/wordsEn.txt");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
