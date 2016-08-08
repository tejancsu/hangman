# Hangman Starter Code for Java 8
**Build environment** -
```
~/hangman  master 1h26m ⚑ ◒
▶ java -version
java version "1.8.0_91"
Java(TM) SE Runtime Environment (build 1.8.0_91-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.91-b14, mixed mode)
```
```
~/hangman  master ✗                                                                                                                                                                             1h26m ⚑ ◒
▶ mvn -version
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T08:41:47-08:00)
Maven home: /usr/local/Cellar/maven/3.3.9/libexec
Java version: 1.8.0_91, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.10.4", arch: "x86_64", family: "mac"
```
-------------
**To compile** -
```
mvn clean install
```

**To run** :
```
java -jar ./target/hangman-1.0-SNAPSHOT.jar HangmanGameDriver 20
```

This produces lot of output showing interaction with the server and at the end you can see statistics,
```
Total number of runs:20
Total wins:18
Win percentage:90
```
-------------

***Algorithm*** :
For each alphabet that has not been used up already, I calculate the probability of getting a hit and use the character with highest probability.

- To calculate the probability of a hit for a given character in a phrase-word, I get the count of all words in the dictionary which match the unmasked characters in the phrase-word and among those words, I get the count of words which contain this particular character. This will give us probability of a hit for a particular character in a phrase-word.

```
P(c/phrase-word) =  (matchingWords which contain c).size/matchingWords.size
matchingWords = Count of all words that match the current phrase
```

- To aggregate the probability of a hit for whole phrase, we just use simple rule in statistics

```
P(A ∪ (B ∪ C)) = P(A) + P(B ∪ C) − P(A ∩ (B ∪ C))
```

- I also use 2 dictionaries for this algorithm, one which contains all words and one which only contains most frequently used words. I fallback to most frequently used words when the probability of last try is very small.

-------------

**Explanation for the low win rate** -

- If the given phrase has 2 letter or 3 letter words like '_at' which matches many words in english dictionary, it becomes a very hard problem unless you have an algorithm which also uses english grammar and contextual meanings.
- A good way to further improve this algorithm is to compare the words before and after a word against well known phrases. There are few research projects to solve the same problem, 
- http://www.sciencedirect.com/science/article/pii/S001048251200042X
