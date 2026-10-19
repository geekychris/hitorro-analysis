<p align="center">
  <img src="assets/logo.png" alt="Hitorro Analysis logo" width="180">
</p>

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

This is a Maven POM-based parent project that can contain multiple analysis-related modules.

## Building

```bash
mvn clean install
```

## Requirements

- Java 19 or higher
- Maven 3.6+

## Key Dependencies

- **Apache Jena**: RDF and semantic web processing
- **JUNG**: Graph visualization and analysis
- **Apache Batik**: SVG generation and rendering
- **Trove4j**: High-performance collections
- **MaltParser**: Dependency parsing

## License

See LICENSE file for details.
