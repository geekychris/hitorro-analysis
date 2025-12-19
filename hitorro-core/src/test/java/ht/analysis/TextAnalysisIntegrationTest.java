package ht.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests demonstrating how multiple components work together
 * for end-to-end text analysis workflows.
 */
@DisplayName("Text Analysis Integration Tests")
class TextAnalysisIntegrationTest {
    
    private TextAnalyzer analyzer;
    private SentenceDetector sentenceDetector;
    
    private static final String SAMPLE_TEXT = 
        "Natural language processing is a fascinating field. " +
        "It combines linguistics, computer science, and artificial intelligence. " +
        "NLP enables computers to understand and generate human language. " +
        "This technology powers many applications we use daily!";
    
    @BeforeEach
    void setUp() {
        analyzer = new TextAnalyzer();
        sentenceDetector = new SentenceDetector();
    }
    
    @Test
    @DisplayName("should perform complete text analysis workflow")
    void shouldPerformCompleteTextAnalysisWorkflow() {
        // Tokenization
        List<String> tokens = analyzer.tokenize(SAMPLE_TEXT);
        assertThat(tokens)
            .isNotEmpty()
            .contains("Natural", "language", "processing");
        
        // Word frequencies
        Map<String, Integer> frequencies = analyzer.wordFrequencies(SAMPLE_TEXT);
        assertThat(frequencies)
            .containsKeys("language", "processing", "computers");
        // Frequencies map size should equal unique word count (may be less than total tokens if there are duplicates)
        assertThat(frequencies.size()).isLessThanOrEqualTo(tokens.size());
        
        // Top words
        List<String> topWords = analyzer.topWords(SAMPLE_TEXT, 5);
        assertThat(topWords)
            .hasSize(5)
            .allMatch(word -> frequencies.containsKey(word));
        
        // Statistics
        TextStatistics stats = analyzer.getStatistics(SAMPLE_TEXT);
        assertThat(stats.getWordCount()).isEqualTo(tokens.size());
        assertThat(stats.getCharacterCount()).isEqualTo(SAMPLE_TEXT.length());
        assertThat(stats.getUniqueWordCount()).isPositive();
        assertThat(stats.getLexicalDiversity()).isBetween(0.0, 1.0);
        
        // Sentence detection
        List<String> sentences = sentenceDetector.detectSentences(SAMPLE_TEXT);
        assertThat(sentences)
            .hasSize(4)
            .allMatch(s -> !s.trim().isEmpty());
        
        int sentenceCount = sentenceDetector.countSentences(SAMPLE_TEXT);
        assertThat(sentenceCount).isEqualTo(sentences.size());
    }
    
    @Test
    @DisplayName("should analyze per-sentence statistics")
    void shouldAnalyzePerSentenceStatistics() {
        List<String> sentences = sentenceDetector.detectSentences(SAMPLE_TEXT);
        
        // Analyze each sentence individually
        for (String sentence : sentences) {
            TextStatistics sentenceStats = analyzer.getStatistics(sentence);
            
            assertThat(sentenceStats.getWordCount())
                .as("Sentence should have words: %s", sentence)
                .isGreaterThan(0);
            
            assertThat(sentenceStats.getLexicalDiversity())
                .as("Lexical diversity should be between 0 and 1")
                .isBetween(0.0, 1.0);
        }
    }
    
    @Test
    @DisplayName("should find common words across sentences")
    void shouldFindCommonWordsAcrossSentences() {
        List<String> sentences = sentenceDetector.detectSentences(SAMPLE_TEXT);
        
        // Get word frequencies for first sentence
        Map<String, Integer> firstSentenceFreqs = analyzer.wordFrequencies(sentences.get(0));
        
        // Get word frequencies for all text
        Map<String, Integer> allTextFreqs = analyzer.wordFrequencies(SAMPLE_TEXT);
        
        // Verify that words from first sentence appear in overall frequencies
        firstSentenceFreqs.keySet().forEach(word -> {
            assertThat(allTextFreqs)
                .as("Word '%s' from first sentence should appear in overall text", word)
                .containsKey(word);
            
            assertThat(allTextFreqs.get(word))
                .as("Frequency in all text should be >= frequency in first sentence")
                .isGreaterThanOrEqualTo(firstSentenceFreqs.get(word));
        });
    }
    
    @Test
    @DisplayName("should handle multi-paragraph text")
    void shouldHandleMultiParagraphText() {
        String multiParagraphText = 
            "Machine learning is transforming technology. " +
            "It enables systems to learn from data.\n\n" +
            "Deep learning is a subset of machine learning. " +
            "It uses neural networks with multiple layers. " +
            "This approach has achieved remarkable results!";
        
        // Analyze overall statistics
        TextStatistics overallStats = analyzer.getStatistics(multiParagraphText);
        assertThat(overallStats.getWordCount()).isGreaterThan(20);
        
        // Detect sentences
        List<String> sentences = sentenceDetector.detectSentences(multiParagraphText);
        assertThat(sentences)
            .hasSize(5)
            .allMatch(s -> s.trim().length() > 0);
        
        // Find most frequent words
        List<String> topWords = analyzer.topWords(multiParagraphText, 3);
        assertThat(topWords)
            .hasSize(3)
            .isNotEmpty();
    }
    
    @Test
    @DisplayName("should calculate aggregate statistics correctly")
    void shouldCalculateAggregateStatisticsCorrectly() {
        List<String> sentences = sentenceDetector.detectSentences(SAMPLE_TEXT);
        
        // Calculate total words across all sentences
        int totalWords = sentences.stream()
            .map(analyzer::tokenize)
            .mapToInt(List::size)
            .sum();
        
        // Should match the overall word count
        TextStatistics overallStats = analyzer.getStatistics(SAMPLE_TEXT);
        assertThat(totalWords)
            .as("Sum of words per sentence should match overall word count")
            .isEqualTo(overallStats.getWordCount());
    }
    
    @Test
    @DisplayName("should identify text complexity indicators")
    void shouldIdentifyTextComplexityIndicators() {
        TextStatistics stats = analyzer.getStatistics(SAMPLE_TEXT);
        int sentenceCount = sentenceDetector.countSentences(SAMPLE_TEXT);
        
        // Calculate average sentence length
        double avgSentenceLength = (double) stats.getWordCount() / sentenceCount;
        assertThat(avgSentenceLength)
            .as("Average sentence length should be positive")
            .isGreaterThan(0);
        
        // Lexical diversity indicates vocabulary richness
        double lexicalDiversity = stats.getLexicalDiversity();
        assertThat(lexicalDiversity)
            .as("Lexical diversity between 0 and 1")
            .isBetween(0.0, 1.0);
        
        // Average word length
        double avgWordLength = stats.getAverageWordLength();
        assertThat(avgWordLength)
            .as("Average word length should be reasonable")
            .isBetween(1.0, 15.0);
    }
    
    @Test
    @DisplayName("should handle custom tokenizer in integration scenario")
    void shouldHandleCustomTokenizerInIntegrationScenario() {
        // Create custom analyzer with whitespace tokenizer
        TokenizerStrategy customTokenizer = new WhitespaceTokenizer();
        TextAnalyzer customAnalyzer = new TextAnalyzer(customTokenizer);
        
        // Perform analysis with custom tokenizer
        List<String> tokens = customAnalyzer.tokenize(SAMPLE_TEXT);
        Map<String, Integer> frequencies = customAnalyzer.wordFrequencies(SAMPLE_TEXT);
        TextStatistics stats = customAnalyzer.getStatistics(SAMPLE_TEXT);
        
        // Verify results are consistent
        assertThat(tokens.size()).isEqualTo(stats.getWordCount());
        assertThat(frequencies.keySet().size()).isEqualTo(stats.getUniqueWordCount());
    }
    
    @Test
    @DisplayName("should produce consistent results across multiple analyses")
    void shouldProduceConsistentResultsAcrossMultipleAnalyses() {
        // Run analysis multiple times
        List<String> tokens1 = analyzer.tokenize(SAMPLE_TEXT);
        List<String> tokens2 = analyzer.tokenize(SAMPLE_TEXT);
        
        assertThat(tokens1).isEqualTo(tokens2);
        
        Map<String, Integer> freq1 = analyzer.wordFrequencies(SAMPLE_TEXT);
        Map<String, Integer> freq2 = analyzer.wordFrequencies(SAMPLE_TEXT);
        
        assertThat(freq1).isEqualTo(freq2);
        
        TextStatistics stats1 = analyzer.getStatistics(SAMPLE_TEXT);
        TextStatistics stats2 = analyzer.getStatistics(SAMPLE_TEXT);
        
        assertThat(stats1).isEqualTo(stats2);
    }
}
