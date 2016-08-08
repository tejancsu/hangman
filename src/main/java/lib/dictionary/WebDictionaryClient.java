package lib.dictionary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Dictionary Client that reads words from URL
 */
public abstract class WebDictionaryClient implements DictionaryClient {
    private static final String DICTIONARY_ENDPOINT = "http://www-01.sil.org/linguistics/wordlists/english/wordlist/wordsEn.txt";

    @Override
    public List<String> getAllWords() {
        List<String> words = new ArrayList<>();
        URL url = null;
        try {
            url = dictionaryEndPoint();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            if (responseCode != HttpURLConnection.HTTP_OK)
                throw new RuntimeException(String.format(
                        "error response (%d)", responseCode));
            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                words.add(inputLine);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return words;
    }

    protected abstract URL dictionaryEndPoint();
}
