# Hitorro Analysis

This is the **hitorro-analysis** project, a standalone Maven-based project for natural language processing, text analysis, parsing, and related functionalities.

## Overview

The Hitorro Analysis project provides comprehensive tools for:

- **Natural Language Processing (NLP)**: Sentence detection, tokenization, part-of-speech tagging, named entity recognition
- **Parsing**: CKY parsing, dependency parsing (MST Parser), Penn Treebank processing
- **Text Classification**: Document categorization and classification
- **Semantic Analysis**: RDF/semantic web processing using Apache Jena
- **Graph Analysis**: Link analysis and conversation tracking using JUNG
- **Machine Learning**: LDA topic modeling, sentiment analysis
- **Vector Operations**: Term vectors, TF-IDF, document similarity

## Project Structure

This is a Maven POM-based parent project that contains multiple analysis-related modules:

- **hitorro-core**: Core text analysis and NLP components
  - Text tokenization and analysis
  - Sentence detection
  - Word frequency analysis
  - Text statistics calculation

## Building

```bash
mvn clean install
```

## Testing

The project includes comprehensive unit tests with **81 tests** covering all functionality.

### Run All Tests

```bash
mvn test
```

### Test Coverage

- **TextAnalyzer**: 24 tests for text analysis and tokenization
- **TextStatistics**: 15 tests for statistics calculations
- **WhitespaceTokenizer**: 12 tests for text tokenization
- **SentenceDetector**: 18 tests for sentence boundary detection
- **Integration Tests**: 12 tests for end-to-end workflows

See [TESTING.md](TESTING.md) for detailed testing documentation and [TEST-SUMMARY.md](TEST-SUMMARY.md) for the latest test results.

## Requirements

- Java 19 or higher
- Maven 3.6+

## Key Dependencies

### Production
- **Apache Jena**: RDF and semantic web processing
- **JUNG**: Graph visualization and analysis
- **Apache Batik**: SVG generation and rendering
- **Trove4j**: High-performance collections
- **MaltParser**: Dependency parsing

### Testing
- **JUnit 5** (Jupiter): Testing framework
- **Mockito**: Mocking framework
- **AssertJ**: Fluent assertions

## License

See LICENSE file for details.
