/*
 * Copyright (c) 2006-2025 Chris Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hitorro.analysis.sentenceenumeration.collectingscorer;

import com.hitorro.analysis.sentenceenumeration.BasicScorer;
import com.hitorro.obj.core.GenericAnalyzer;
import com.hitorro.util.core.longword.WordBits;
import com.hitorro.util.core.map.FPHashBaseMapInterface;

/**
 *
 */
public class CollectingPhraseScorer extends BasicScorer<CollectingSentenceWrapper, CollectingEmitter> {
    private int maxHits;

    public CollectingPhraseScorer(GenericAnalyzer analyzer, WordBits wb, FPHashBaseMapInterface map, int maxHits) {
        super();
        this.maxHits = maxHits;
        init(map, wb, analyzer);
    }

    public CollectingSentenceWrapper getWrapper() {
        return new CollectingSentenceWrapper();
    }

    public CollectingEmitter getNewBaseEmitter() {
        return new CollectingEmitter(4, dict, recordStrings, maxHits);
    }
}
