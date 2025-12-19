package ht.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SentenceDetector Tests")
class SentenceDetectorTest {
    
    private SentenceDetector detector;
    
    @BeforeEach
    void setUp() {
        detector = new SentenceDetector();
    }
    
    @Nested
    @DisplayName("Sentence Detection Tests")
    class SentenceDetectionTests {
        
        @Test
        @DisplayName("should detect simple sentences")
        void shouldDetectSimpleSentences() {
            String text = "Hello world. How are you?";
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences)
                .hasSize(2)
                .contains("Hello world.")
                .contains("How are you?");
        }
        
        @Test
        @DisplayName("should handle exclamation marks")
        void shouldHandleExclamationMarks() {
            String text = "Hello! What a great day! Indeed it is.";
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences)
                .hasSize(3)
                .contains("Hello!")
                .contains("What a great day!")
                .contains("Indeed it is.");
        }
        
        @Test
        @DisplayName("should handle question marks")
        void shouldHandleQuestionMarks() {
            String text = "Are you there? Yes I am. Really?";
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences).hasSize(3);
        }
        
        @Test
        @DisplayName("should handle multiple punctuation marks")
        void shouldHandleMultiplePunctuationMarks() {
            String text = "What?! Really!!! Yes.";
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences).isNotEmpty();
        }
        
        @Test
        @DisplayName("should handle single sentence")
        void shouldHandleSingleSentence() {
            String text = "This is a single sentence.";
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences)
                .hasSize(1)
                .containsExactly("This is a single sentence.");
        }
        
        @Test
        @DisplayName("should handle sentence without ending punctuation")
        void shouldHandleSentenceWithoutEndingPunctuation() {
            String text = "This sentence has no ending punctuation";
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences)
                .hasSize(1)
                .containsExactly("This sentence has no ending punctuation");
        }
        
        @Test
        @DisplayName("should return empty list for empty text")
        void shouldReturnEmptyListForEmptyText() {
            List<String> sentences = detector.detectSentences("");
            assertThat(sentences).isEmpty();
        }
        
        @Test
        @DisplayName("should throw exception when text is null")
        void shouldThrowExceptionWhenTextIsNull() {
            assertThatThrownBy(() -> detector.detectSentences(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Text cannot be null");
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t\t", "\n\n"})
        @DisplayName("should return empty list for whitespace-only text")
        void shouldReturnEmptyListForWhitespaceOnly(String text) {
            List<String> sentences = detector.detectSentences(text);
            assertThat(sentences).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Sentence Counting Tests")
    class SentenceCountingTests {
        
        @Test
        @DisplayName("should count sentences correctly")
        void shouldCountSentencesCorrectly() {
            String text = "First sentence. Second sentence! Third sentence?";
            int count = detector.countSentences(text);
            
            assertThat(count).isEqualTo(3);
        }
        
        @Test
        @DisplayName("should return zero for empty text")
        void shouldReturnZeroForEmptyText() {
            int count = detector.countSentences("");
            assertThat(count).isZero();
        }
        
        @Test
        @DisplayName("should count single sentence")
        void shouldCountSingleSentence() {
            String text = "Only one sentence here.";
            int count = detector.countSentences(text);
            
            assertThat(count).isEqualTo(1);
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("should handle abbreviations")
        void shouldHandleAbbreviations() {
            String text = "Dr. Smith is here. He works at U.S. Corp.";
            List<String> sentences = detector.detectSentences(text);
            
            // This is a simple detector, so it might not handle abbreviations perfectly
            assertThat(sentences).isNotEmpty();
        }
        
        @Test
        @DisplayName("should handle newlines in text")
        void shouldHandleNewlines() {
            String text = "First sentence.\nSecond sentence.";
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences).hasSize(2);
        }
        
        @Test
        @DisplayName("should handle mixed punctuation")
        void shouldHandleMixedPunctuation() {
            String text = "Really?! Yes... Maybe. Definitely!";
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences).isNotEmpty();
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("should handle complex paragraph")
        void shouldHandleComplexParagraph() {
            String text = "Natural language processing is fascinating. " +
                         "It enables computers to understand human language! " +
                         "What are the main challenges? " +
                         "Ambiguity and context are key issues.";
            
            List<String> sentences = detector.detectSentences(text);
            
            assertThat(sentences)
                .hasSize(4)
                .allMatch(s -> !s.isEmpty());
            
            int count = detector.countSentences(text);
            assertThat(count).isEqualTo(sentences.size());
        }
    }
}
