package ht.analysis;

import java.util.Objects;

/**
 * Immutable class representing text statistics.
 */
public class TextStatistics {
    
    private final int wordCount;
    private final int characterCount;
    private final int uniqueWordCount;
    private final int sentenceCount;
    
    public TextStatistics(int wordCount, int characterCount, int uniqueWordCount, int sentenceCount) {
        if (wordCount < 0 || characterCount < 0 || uniqueWordCount < 0 || sentenceCount < 0) {
            throw new IllegalArgumentException("Statistics values cannot be negative");
        }
        
        this.wordCount = wordCount;
        this.characterCount = characterCount;
        this.uniqueWordCount = uniqueWordCount;
        this.sentenceCount = sentenceCount;
    }
    
    public int getWordCount() {
        return wordCount;
    }
    
    public int getCharacterCount() {
        return characterCount;
    }
    
    public int getUniqueWordCount() {
        return uniqueWordCount;
    }
    
    public int getSentenceCount() {
        return sentenceCount;
    }
    
    public double getAverageWordLength() {
        return wordCount > 0 ? (double) characterCount / wordCount : 0.0;
    }
    
    public double getLexicalDiversity() {
        return wordCount > 0 ? (double) uniqueWordCount / wordCount : 0.0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextStatistics that = (TextStatistics) o;
        return wordCount == that.wordCount &&
               characterCount == that.characterCount &&
               uniqueWordCount == that.uniqueWordCount &&
               sentenceCount == that.sentenceCount;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(wordCount, characterCount, uniqueWordCount, sentenceCount);
    }
    
    @Override
    public String toString() {
        return String.format("TextStatistics{words=%d, chars=%d, unique=%d, sentences=%d, " +
                           "avgWordLen=%.2f, lexicalDiv=%.2f}",
                           wordCount, characterCount, uniqueWordCount, sentenceCount,
                           getAverageWordLength(), getLexicalDiversity());
    }
}
