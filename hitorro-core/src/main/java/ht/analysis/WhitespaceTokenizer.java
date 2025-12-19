package ht.analysis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple tokenizer that splits text on whitespace.
 */
public class WhitespaceTokenizer implements TokenizerStrategy {
    
    @Override
    public List<String> tokenize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(text.trim().split("\\s+"))
            .filter(token -> !token.isEmpty())
            .collect(Collectors.toList());
    }
}
