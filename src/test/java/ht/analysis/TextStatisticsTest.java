package ht.analysis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TextStatistics Tests")
class TextStatisticsTest {
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("should create statistics with valid values")
        void shouldCreateWithValidValues() {
            TextStatistics stats = new TextStatistics(10, 50, 8, 2);
            
            assertThat(stats.getWordCount()).isEqualTo(10);
            assertThat(stats.getCharacterCount()).isEqualTo(50);
            assertThat(stats.getUniqueWordCount()).isEqualTo(8);
            assertThat(stats.getSentenceCount()).isEqualTo(2);
        }
        
        @Test
        @DisplayName("should throw exception when word count is negative")
        void shouldThrowExceptionWhenWordCountIsNegative() {
            assertThatThrownBy(() -> new TextStatistics(-1, 50, 8, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Statistics values cannot be negative");
        }
        
        @Test
        @DisplayName("should throw exception when character count is negative")
        void shouldThrowExceptionWhenCharacterCountIsNegative() {
            assertThatThrownBy(() -> new TextStatistics(10, -1, 8, 2))
                .isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        @DisplayName("should allow zero values")
        void shouldAllowZeroValues() {
            TextStatistics stats = new TextStatistics(0, 0, 0, 0);
            
            assertThat(stats.getWordCount()).isZero();
            assertThat(stats.getCharacterCount()).isZero();
            assertThat(stats.getUniqueWordCount()).isZero();
            assertThat(stats.getSentenceCount()).isZero();
        }
    }
    
    @Nested
    @DisplayName("Calculation Tests")
    class CalculationTests {
        
        @Test
        @DisplayName("should calculate average word length correctly")
        void shouldCalculateAverageWordLength() {
            TextStatistics stats = new TextStatistics(10, 50, 8, 2);
            
            assertThat(stats.getAverageWordLength()).isEqualTo(5.0);
        }
        
        @Test
        @DisplayName("should return zero average for no words")
        void shouldReturnZeroAverageForNoWords() {
            TextStatistics stats = new TextStatistics(0, 0, 0, 0);
            
            assertThat(stats.getAverageWordLength()).isZero();
        }
        
        @Test
        @DisplayName("should calculate lexical diversity correctly")
        void shouldCalculateLexicalDiversity() {
            TextStatistics stats = new TextStatistics(10, 50, 8, 2);
            
            assertThat(stats.getLexicalDiversity()).isEqualTo(0.8);
        }
        
        @Test
        @DisplayName("should return zero lexical diversity for no words")
        void shouldReturnZeroLexicalDiversityForNoWords() {
            TextStatistics stats = new TextStatistics(0, 0, 0, 0);
            
            assertThat(stats.getLexicalDiversity()).isZero();
        }
        
        @Test
        @DisplayName("should have lexical diversity of 1.0 when all words are unique")
        void shouldHavePerfectDiversityWhenAllWordsUnique() {
            TextStatistics stats = new TextStatistics(10, 50, 10, 2);
            
            assertThat(stats.getLexicalDiversity()).isEqualTo(1.0);
        }
    }
    
    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            TextStatistics stats1 = new TextStatistics(10, 50, 8, 2);
            TextStatistics stats2 = new TextStatistics(10, 50, 8, 2);
            
            assertThat(stats1).isEqualTo(stats2);
            assertThat(stats1.hashCode()).isEqualTo(stats2.hashCode());
        }
        
        @Test
        @DisplayName("should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            TextStatistics stats1 = new TextStatistics(10, 50, 8, 2);
            TextStatistics stats2 = new TextStatistics(10, 50, 8, 3);
            
            assertThat(stats1).isNotEqualTo(stats2);
        }
        
        @Test
        @DisplayName("should be equal to itself")
        void shouldBeEqualToItself() {
            TextStatistics stats = new TextStatistics(10, 50, 8, 2);
            
            assertThat(stats).isEqualTo(stats);
        }
        
        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            TextStatistics stats = new TextStatistics(10, 50, 8, 2);
            
            assertThat(stats).isNotEqualTo(null);
        }
        
        @Test
        @DisplayName("should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            TextStatistics stats = new TextStatistics(10, 50, 8, 2);
            
            assertThat(stats).isNotEqualTo("not a TextStatistics");
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("should return informative string representation")
        void shouldReturnInformativeStringRepresentation() {
            TextStatistics stats = new TextStatistics(10, 50, 8, 2);
            String str = stats.toString();
            
            assertThat(str)
                .contains("TextStatistics")
                .contains("words=10")
                .contains("chars=50")
                .contains("unique=8")
                .contains("sentences=2")
                .contains("avgWordLen=5.00")
                .contains("lexicalDiv=0.80");
        }
    }
}
