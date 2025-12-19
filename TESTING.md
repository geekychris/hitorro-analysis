# Testing Guide for Hitorro Analysis

This document provides comprehensive information about the testing framework and best practices for the Hitorro Analysis project.

## Overview

The project uses **JUnit 5** (Jupiter) as the primary testing framework, along with:
- **Mockito** for mocking and test doubles
- **AssertJ** for fluent assertions
- **Maven Surefire** for test execution

## Test Structure

### Current Test Coverage

The `hitorro-core` module includes comprehensive unit tests for:

1. **TextAnalyzerTest** - Tests for text analysis functionality
   - Tokenization
   - Word frequency analysis
   - Top words extraction
   - Text statistics generation
   - Custom tokenizer strategies

2. **TextStatisticsTest** - Tests for text statistics
   - Constructor validation
   - Statistical calculations (average word length, lexical diversity)
   - Equality and hashCode
   - String representation

3. **WhitespaceTokenizerTest** - Tests for tokenization
   - Simple text tokenization
   - Edge cases (empty, null, whitespace-only)
   - Punctuation handling

4. **SentenceDetectorTest** - Tests for sentence detection
   - Sentence boundary detection
   - Sentence counting
   - Edge cases with punctuation
   - Complex paragraph handling

### Test Statistics

```
Total Tests: 69
- Passed: 69
- Failed: 0
- Skipped: 0
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Tests for Specific Module

```bash
mvn test -pl hitorro-core
```

### Run Specific Test Class

```bash
mvn test -Dtest=TextAnalyzerTest
```

### Run Specific Test Method

```bash
mvn test -Dtest=TextAnalyzerTest#shouldTokenizeSimpleText
```

### Run with Verbose Output

```bash
mvn test -X
```

### Generate Test Report

```bash
mvn surefire-report:report
```

## Test Organization

### Using @Nested for Logical Grouping

Tests are organized using JUnit 5's `@Nested` annotation to group related tests:

```java
@DisplayName("TextAnalyzer Tests")
class TextAnalyzerTest {
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        // Constructor-related tests
    }
    
    @Nested
    @DisplayName("Tokenization Tests")
    class TokenizationTests {
        // Tokenization-related tests
    }
}
```

### Using @DisplayName for Readability

All tests use descriptive display names:

```java
@Test
@DisplayName("should tokenize simple text")
void shouldTokenizeSimpleText() {
    // Test implementation
}
```

## Testing Best Practices

### 1. Arrange-Act-Assert (AAA) Pattern

```java
@Test
void shouldCalculateWordFrequencies() {
    // Arrange
    String text = "hello world hello";
    
    // Act
    Map<String, Integer> frequencies = analyzer.wordFrequencies(text);
    
    // Assert
    assertThat(frequencies)
        .containsEntry("hello", 2)
        .containsEntry("world", 1);
}
```

### 2. Use AssertJ for Fluent Assertions

Instead of JUnit assertions, use AssertJ for better readability:

```java
// Good - AssertJ
assertThat(tokens)
    .hasSize(2)
    .containsExactly("hello", "world");

// Less readable - JUnit
assertEquals(2, tokens.size());
assertTrue(tokens.contains("hello"));
assertTrue(tokens.contains("world"));
```

### 3. Test Edge Cases

Always test:
- Null inputs
- Empty inputs
- Boundary conditions
- Invalid inputs
- Large inputs

```java
@Test
@DisplayName("should throw exception when text is null")
void shouldThrowExceptionWhenTextIsNull() {
    assertThatThrownBy(() -> analyzer.tokenize(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Text cannot be null");
}
```

### 4. Use Parameterized Tests

For testing multiple similar cases:

```java
@ParameterizedTest
@ValueSource(strings = {
    "The quick brown fox",
    "Java programming is fun",
    "Natural language processing"
})
@DisplayName("should handle various text inputs")
void shouldHandleVariousInputs(String text) {
    List<String> tokens = analyzer.tokenize(text);
    assertThat(tokens).isNotEmpty();
}
```

### 5. Use Mocking Appropriately

Use Mockito for testing with dependencies:

```java
@Mock
private TokenizerStrategy mockTokenizer;

@Test
void shouldUseCustomTokenizerStrategy() {
    when(mockTokenizer.tokenize(anyString()))
        .thenReturn(Arrays.asList("custom", "tokens"));
    
    TextAnalyzer customAnalyzer = new TextAnalyzer(mockTokenizer);
    List<String> tokens = customAnalyzer.tokenize("test");
    
    verify(mockTokenizer).tokenize("test");
}
```

### 6. Test Behavior, Not Implementation

Focus on what the code does, not how it does it:

```java
// Good - tests behavior
@Test
void shouldReturnTopThreeWords() {
    List<String> topWords = analyzer.topWords(text, 3);
    assertThat(topWords).hasSize(3);
}

// Bad - tests implementation details
@Test
void shouldUseStreamAPIForTopWords() {
    // Don't test internal implementation
}
```

## Code Coverage

To generate code coverage reports, add the JaCoCo plugin to your module's POM:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Then run:

```bash
mvn clean test jacoco:report
```

## Continuous Integration

### GitHub Actions Example

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 19
        uses: actions/setup-java@v3
        with:
          java-version: '19'
          distribution: 'temurin'
      - name: Run tests
        run: mvn test
```

## Adding Tests for New Features

When adding new functionality:

1. **Write tests first** (TDD approach) or immediately after implementation
2. **Test public APIs** - Focus on the public interface
3. **Include edge cases** - Null, empty, boundary conditions
4. **Document with @DisplayName** - Make test intent clear
5. **Group related tests** - Use @Nested classes
6. **Maintain test independence** - Each test should be runnable in isolation

### Example Test Template

```java
package ht.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

@DisplayName("YourClass Tests")
class YourClassTest {
    
    private YourClass instance;
    
    @BeforeEach
    void setUp() {
        instance = new YourClass();
    }
    
    @Nested
    @DisplayName("Feature Tests")
    class FeatureTests {
        
        @Test
        @DisplayName("should do something correctly")
        void shouldDoSomethingCorrectly() {
            // Arrange
            String input = "test input";
            
            // Act
            String result = instance.doSomething(input);
            
            // Assert
            assertThat(result).isEqualTo("expected output");
        }
    }
}
```

## Troubleshooting

### Common Issues

1. **Tests not found**
   - Ensure test classes end with `Test` or `Tests`
   - Check that tests are in `src/test/java`
   - Verify JUnit 5 dependencies are present

2. **Mockito errors**
   - Initialize mocks with `MockitoAnnotations.openMocks(this)` in `@BeforeEach`
   - Ensure mockito-junit-jupiter dependency is included

3. **Compilation errors**
   - Check Java version compatibility (Java 19 required)
   - Verify all dependencies are resolved

## Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
