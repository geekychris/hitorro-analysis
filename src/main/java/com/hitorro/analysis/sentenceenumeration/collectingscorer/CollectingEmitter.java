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

import com.hitorro.analysis.sentenceenumeration.BaseEmitter;
import com.hitorro.basetext.dfindex.FPHashDict;

/**
 * Emitter that keeps track of the elements emitted and can be used to
 */
public class CollectingEmitter extends BaseEmitter {
    PhraseCollection pc;

    private float score;

    public CollectingEmitter(int maxDepth, FPHashDict baseIndex, boolean recordStrings, int maxHits) {
        super(maxDepth, baseIndex, recordStrings);
        pc = new PhraseCollection(maxHits);
    }

    public void reset() {
        super.reset();
        score = 0;
        pc.reset();
    }

    @Override
    public boolean participate() {
        return pc.getSize() > 0;
    }

    @Override
    public float getScore() {
        return score;
    }

    public void phraseMatch(long fingerPrint, int startPosition, int size, String txt, long val, int layer, int charStartOffset, int charEndOffset) {
        //XXX this is dumb, simply counting hits
        score++;
        pc.phraseMatch(fingerPrint, startPosition, size, txt, val, layer, charStartOffset, charEndOffset);
    }
}
