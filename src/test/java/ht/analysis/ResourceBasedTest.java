package ht.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * Example test demonstrating how to use test resources for testing.
 * This pattern is useful when testing with larger text files or sample data.
 */
@DisplayName("Resource-Based Tests")
class ResourceBasedTest {
    
    private TextAnalyzer analyzer;
    private SentenceDetector sentenceDetector;
    
    @BeforeEach
    void setUp() {
        analyzer = new TextAnalyzer();
        sentenceDetector = new SentenceDetector();
    }
    
    @Test
    @DisplayName("should analyze text loaded from resource file")
    void shouldAnalyzeTextFromResourceFile() throws IOException {
        // Load text from test resources
        String text = loadResourceFile("sample-text.txt");
        
        assertThat(text)
            .isNotNull()
            .isNotEmpty();
        
        // Perform analysis
        List<String> tokens = analyzer.tokenize(text);
        TextStatistics stats = analyzer.getStatistics(text);
        List<String> sentences = sentenceDetector.detectSentences(text);
        
        // Verify results
        assertThat(tokens)
            .hasSizeGreaterThan(20)
            .contains("quick", "brown", "fox", "processing");
        
        assertThat(stats.getWordCount()).isGreaterThan(20);
        assertThat(stats.getUniqueWordCount()).isGreaterThan(15);
        assertThat(stats.getLexicalDiversity()).isBetween(0.5, 1.0);
        
        assertThat(sentences)
            .hasSize(4)
            .allMatch(s -> s.trim().length() > 0);
    }
    
    @Test
    @DisplayName("should handle multi-line resource content")
    void shouldHandleMultiLineResourceContent() throws IOException {
        String text = loadResourceFile("sample-text.txt");
        
        // Verify newlines are preserved or handled correctly
        assertThat(text).contains("\n");
        
        // Tokenization should work across lines
        List<String> tokens = analyzer.tokenize(text);
        assertThat(tokens)
            .doesNotContain("\n")
            .allMatch(token -> !token.contains("\n"));
    }
    
    @Test
    @DisplayName("should calculate accurate statistics for known text")
    void shouldCalculateAccurateStatisticsForKnownText() throws IOException {
        String text = loadResourceFile("sample-text.txt");
        
        // The sample file has 4 sentences
        int sentenceCount = sentenceDetector.countSentences(text);
        assertThat(sentenceCount).isEqualTo(4);
        
        // Calculate statistics
        TextStatistics stats = analyzer.getStatistics(text);
        
        // Verify reasonable values
        assertThat(stats.getAverageWordLength())
            .as("Average English word length should be between 3 and 7 characters")
            .isBetween(3.0, 7.0);
        
        assertThat(stats.getCharacterCount())
            .as("Character count should include all characters")
            .isGreaterThan(100);
    }
    
    @Test
    @DisplayName("should identify most frequent words in resource text")
    void shouldIdentifyMostFrequentWordsInResourceText() throws IOException {
        String text = loadResourceFile("sample-text.txt");
        
        // Get top 5 words
        List<String> topWords = analyzer.topWords(text, 5);
        
        assertThat(topWords)
            .hasSize(5)
            .isNotEmpty();
        
        // Verify these are actual words from the text
        List<String> allTokens = analyzer.tokenize(text);
        assertThat(topWords).isSubsetOf(allTokens);
    }
    
    /**
     * Helper method to load text from test resources.
     * 
     * @param resourceName name of the resource file
     * @return content of the file
     * @throws IOException if file cannot be read
     */
    private String loadResourceFile(String resourceName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(is)
                .as("Resource file '%s' should exist", resourceName)
                .isNotNull();
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines()
                    .collect(Collectors.joining("\n"));
            }
        }
    }
}
