package ht.analysis;

import java.util.List;

/**
 * Strategy interface for text tokenization.
 */
public interface TokenizerStrategy {
    
    /**
     * Tokenizes the input text.
     * 
     * @param text the text to tokenize
     * @return list of tokens
     */
    List<String> tokenize(String text);
}
