package ht.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("WhitespaceTokenizer Tests")
class WhitespaceTokenizerTest {
    
    private WhitespaceTokenizer tokenizer;
    
    @BeforeEach
    void setUp() {
        tokenizer = new WhitespaceTokenizer();
    }
    
    @Test
    @DisplayName("should tokenize simple text")
    void shouldTokenizeSimpleText() {
        String text = "hello world";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertThat(tokens)
            .hasSize(2)
            .containsExactly("hello", "world");
    }
    
    @Test
    @DisplayName("should handle multiple spaces")
    void shouldHandleMultipleSpaces() {
        String text = "hello    world   test";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertThat(tokens)
            .hasSize(3)
            .containsExactly("hello", "world", "test");
    }
    
    @Test
    @DisplayName("should handle leading and trailing whitespace")
    void shouldHandleLeadingAndTrailingWhitespace() {
        String text = "   hello world   ";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertThat(tokens)
            .hasSize(2)
            .containsExactly("hello", "world");
    }
    
    @Test
    @DisplayName("should handle tabs and newlines")
    void shouldHandleTabsAndNewlines() {
        String text = "hello\tworld\ntest";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertThat(tokens)
            .hasSize(3)
            .containsExactly("hello", "world", "test");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\t", "\n\n"})
    @DisplayName("should return empty list for null or whitespace-only text")
    void shouldReturnEmptyListForNullOrWhitespace(String text) {
        List<String> tokens = tokenizer.tokenize(text);
        assertThat(tokens).isEmpty();
    }
    
    @Test
    @DisplayName("should handle single word")
    void shouldHandleSingleWord() {
        String text = "hello";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertThat(tokens)
            .hasSize(1)
            .containsExactly("hello");
    }
    
    @Test
    @DisplayName("should preserve punctuation attached to words")
    void shouldPreservePunctuationAttachedToWords() {
        String text = "hello, world!";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertThat(tokens)
            .hasSize(2)
            .containsExactly("hello,", "world!");
    }
    
    @Test
    @DisplayName("should handle mixed whitespace characters")
    void shouldHandleMixedWhitespace() {
        String text = "hello \t world \n test \r\n end";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertThat(tokens)
            .hasSize(4)
            .containsExactly("hello", "world", "test", "end");
    }
}
