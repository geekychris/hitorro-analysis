package ht.analysis;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides text analysis functionality including tokenization,
 * word frequency analysis, and basic text statistics.
 */
public class TextAnalyzer {
    
    private final TokenizerStrategy tokenizerStrategy;
    
    public TextAnalyzer() {
        this(new WhitespaceTokenizer());
    }
    
    public TextAnalyzer(TokenizerStrategy tokenizerStrategy) {
        this.tokenizerStrategy = Objects.requireNonNull(tokenizerStrategy, 
            "Tokenizer strategy cannot be null");
    }
    
    /**
     * Tokenizes the input text into words.
     * 
     * @param text the text to tokenize
     * @return list of tokens
     * @throws IllegalArgumentException if text is null
     */
    public List<String> tokenize(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        return tokenizerStrategy.tokenize(text);
    }
    
    /**
     * Counts word frequencies in the given text.
     * 
     * @param text the text to analyze
     * @return map of words to their frequencies
     */
    public Map<String, Integer> wordFrequencies(String text) {
        List<String> tokens = tokenize(text);
        Map<String, Integer> frequencies = new HashMap<>();
        
        for (String token : tokens) {
            frequencies.merge(token, 1, Integer::sum);
        }
        
        return frequencies;
    }
    
    /**
     * Returns the most frequent words in the text.
     * 
     * @param text the text to analyze
     * @param n number of top words to return
     * @return list of most frequent words
     */
    public List<String> topWords(String text, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        
        Map<String, Integer> frequencies = wordFrequencies(text);
        
        return frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(n)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculates basic text statistics.
     * 
     * @param text the text to analyze
     * @return statistics object
     */
    public TextStatistics getStatistics(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        List<String> tokens = tokenize(text);
        int wordCount = tokens.size();
        int charCount = text.length();
        int uniqueWords = new HashSet<>(tokens).size();
        
        // Count sentences (simple approach: periods, exclamation marks, question marks)
        int sentenceCount = text.split("[.!?]+").length;
        
        return new TextStatistics(wordCount, charCount, uniqueWords, sentenceCount);
    }
}
