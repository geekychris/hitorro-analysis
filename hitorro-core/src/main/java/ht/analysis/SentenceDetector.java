package ht.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects sentence boundaries in text.
 */
public class SentenceDetector {
    
    // Pattern for sentence boundaries: periods, exclamation marks, question marks
    // followed by whitespace and a capital letter
    private static final Pattern SENTENCE_PATTERN = 
        Pattern.compile("([.!?]+)\\s+(?=[A-Z])");
    
    /**
     * Splits text into sentences.
     * 
     * @param text the text to split
     * @return list of sentences
     * @throws IllegalArgumentException if text is null
     */
    public List<String> detectSentences(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        if (text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> sentences = new ArrayList<>();
        Matcher matcher = SENTENCE_PATTERN.matcher(text);
        
        int lastEnd = 0;
        while (matcher.find()) {
            String sentence = text.substring(lastEnd, matcher.end()).trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
            lastEnd = matcher.end();
        }
        
        // Add the last sentence if there's remaining text
        if (lastEnd < text.length()) {
            String lastSentence = text.substring(lastEnd).trim();
            if (!lastSentence.isEmpty()) {
                sentences.add(lastSentence);
            }
        }
        
        return sentences;
    }
    
    /**
     * Counts the number of sentences in the text.
     * 
     * @param text the text to analyze
     * @return number of sentences
     */
    public int countSentences(String text) {
        return detectSentences(text).size();
    }
}
