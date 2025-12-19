package ht.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TextAnalyzer Tests")
class TextAnalyzerTest {
    
    private TextAnalyzer analyzer;
    
    @Mock
    private TokenizerStrategy mockTokenizer;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        analyzer = new TextAnalyzer();
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("should create analyzer with default tokenizer")
        void shouldCreateWithDefaultTokenizer() {
            TextAnalyzer defaultAnalyzer = new TextAnalyzer();
            assertThat(defaultAnalyzer).isNotNull();
        }
        
        @Test
        @DisplayName("should create analyzer with custom tokenizer")
        void shouldCreateWithCustomTokenizer() {
            TextAnalyzer customAnalyzer = new TextAnalyzer(mockTokenizer);
            assertThat(customAnalyzer).isNotNull();
        }
        
        @Test
        @DisplayName("should throw exception when tokenizer is null")
        void shouldThrowExceptionWhenTokenizerIsNull() {
            assertThatThrownBy(() -> new TextAnalyzer(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Tokenizer strategy cannot be null");
        }
    }
    
    @Nested
    @DisplayName("Tokenization Tests")
    class TokenizationTests {
        
        @Test
        @DisplayName("should tokenize simple text")
        void shouldTokenizeSimpleText() {
            String text = "hello world";
            List<String> tokens = analyzer.tokenize(text);
            
            assertThat(tokens)
                .hasSize(2)
                .containsExactly("hello", "world");
        }
        
        @Test
        @DisplayName("should tokenize text with multiple spaces")
        void shouldTokenizeWithMultipleSpaces() {
            String text = "hello    world   test";
            List<String> tokens = analyzer.tokenize(text);
            
            assertThat(tokens)
                .hasSize(3)
                .containsExactly("hello", "world", "test");
        }
        
        @Test
        @DisplayName("should handle empty text")
        void shouldHandleEmptyText() {
            List<String> tokens = analyzer.tokenize("");
            assertThat(tokens).isEmpty();
        }
        
        @Test
        @DisplayName("should throw exception when text is null")
        void shouldThrowExceptionWhenTextIsNull() {
            assertThatThrownBy(() -> analyzer.tokenize(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Text cannot be null");
        }
        
        @Test
        @DisplayName("should use custom tokenizer strategy")
        void shouldUseCustomTokenizerStrategy() {
            when(mockTokenizer.tokenize(anyString()))
                .thenReturn(Arrays.asList("custom", "tokens"));
            
            TextAnalyzer customAnalyzer = new TextAnalyzer(mockTokenizer);
            List<String> tokens = customAnalyzer.tokenize("test text");
            
            assertThat(tokens).containsExactly("custom", "tokens");
            verify(mockTokenizer).tokenize("test text");
        }
    }
    
    @Nested
    @DisplayName("Word Frequency Tests")
    class WordFrequencyTests {
        
        @Test
        @DisplayName("should count word frequencies correctly")
        void shouldCountWordFrequencies() {
            String text = "hello world hello test world hello";
            Map<String, Integer> frequencies = analyzer.wordFrequencies(text);
            
            assertThat(frequencies)
                .hasSize(3)
                .containsEntry("hello", 3)
                .containsEntry("world", 2)
                .containsEntry("test", 1);
        }
        
        @Test
        @DisplayName("should return empty map for empty text")
        void shouldReturnEmptyMapForEmptyText() {
            Map<String, Integer> frequencies = analyzer.wordFrequencies("");
            assertThat(frequencies).isEmpty();
        }
        
        @Test
        @DisplayName("should handle single word")
        void shouldHandleSingleWord() {
            String text = "hello";
            Map<String, Integer> frequencies = analyzer.wordFrequencies(text);
            
            assertThat(frequencies)
                .hasSize(1)
                .containsEntry("hello", 1);
        }
    }
    
    @Nested
    @DisplayName("Top Words Tests")
    class TopWordsTests {
        
        @Test
        @DisplayName("should return top N words")
        void shouldReturnTopNWords() {
            String text = "apple apple apple banana banana cherry";
            List<String> topWords = analyzer.topWords(text, 2);
            
            assertThat(topWords)
                .hasSize(2)
                .containsExactly("apple", "banana");
        }
        
        @Test
        @DisplayName("should return all words when N is greater than word count")
        void shouldReturnAllWordsWhenNIsGreater() {
            String text = "apple banana";
            List<String> topWords = analyzer.topWords(text, 10);
            
            assertThat(topWords).hasSize(2);
        }
        
        @Test
        @DisplayName("should return empty list when N is zero")
        void shouldReturnEmptyListWhenNIsZero() {
            String text = "apple banana cherry";
            List<String> topWords = analyzer.topWords(text, 0);
            
            assertThat(topWords).isEmpty();
        }
        
        @Test
        @DisplayName("should throw exception when N is negative")
        void shouldThrowExceptionWhenNIsNegative() {
            assertThatThrownBy(() -> analyzer.topWords("test", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("n must be non-negative");
        }
        
        @Test
        @DisplayName("should handle empty text")
        void shouldHandleEmptyText() {
            List<String> topWords = analyzer.topWords("", 5);
            assertThat(topWords).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {
        
        @Test
        @DisplayName("should calculate basic statistics correctly")
        void shouldCalculateBasicStatistics() {
            String text = "Hello world! This is a test. Another sentence here.";
            TextStatistics stats = analyzer.getStatistics(text);
            
            assertThat(stats.getWordCount()).isEqualTo(9);
            assertThat(stats.getCharacterCount()).isEqualTo(text.length());
            assertThat(stats.getUniqueWordCount()).isEqualTo(9); // All words are unique
            assertThat(stats.getSentenceCount()).isGreaterThan(0);
        }
        
        @Test
        @DisplayName("should calculate statistics for repeated words")
        void shouldCalculateStatsForRepeatedWords() {
            String text = "test test test";
            TextStatistics stats = analyzer.getStatistics(text);
            
            assertThat(stats.getWordCount()).isEqualTo(3);
            assertThat(stats.getUniqueWordCount()).isEqualTo(1);
        }
        
        @Test
        @DisplayName("should handle empty text")
        void shouldHandleEmptyText() {
            String text = "";
            TextStatistics stats = analyzer.getStatistics(text);
            
            assertThat(stats.getWordCount()).isZero();
            assertThat(stats.getCharacterCount()).isZero();
            assertThat(stats.getUniqueWordCount()).isZero();
        }
        
        @Test
        @DisplayName("should throw exception when text is null")
        void shouldThrowExceptionWhenTextIsNull() {
            assertThatThrownBy(() -> analyzer.getStatistics(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Text cannot be null");
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @ParameterizedTest
        @ValueSource(strings = {
            "The quick brown fox jumps over the lazy dog",
            "Java programming is fun and educational",
            "Natural language processing enables machines to understand text"
        })
        @DisplayName("should handle various text inputs")
        void shouldHandleVariousTextInputs(String text) {
            List<String> tokens = analyzer.tokenize(text);
            Map<String, Integer> frequencies = analyzer.wordFrequencies(text);
            TextStatistics stats = analyzer.getStatistics(text);
            
            assertThat(tokens).isNotEmpty();
            assertThat(frequencies).isNotEmpty();
            assertThat(stats.getWordCount()).isEqualTo(tokens.size());
        }
        
        @Test
        @DisplayName("should handle complex text with punctuation")
        void shouldHandleComplexTextWithPunctuation() {
            String text = "Hello, world! How are you? I'm fine, thanks.";
            
            TextStatistics stats = analyzer.getStatistics(text);
            assertThat(stats.getWordCount()).isGreaterThan(0);
            assertThat(stats.getSentenceCount()).isGreaterThan(0);
        }
    }
}
