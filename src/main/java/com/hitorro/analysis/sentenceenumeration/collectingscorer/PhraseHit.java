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

import com.hitorro.util.core.string.Fmt;


public class PhraseHit {
    public long fingerPrint;
    public String txt;
    public int startPosition;
    public int size;
    public long val;
    public int layer;

    public int charStartOffset;
    public int charEndOffset;

    public long actualValue;
    public String name;

    public PhraseHit() {

    }

    /**
     * Copy constructor
     *
     * @param pc
     */
    public PhraseHit(PhraseHit pc) {
        copy(pc);
    }

    public String toString() {
        return Fmt.S("%s fp:%s sp: %s, size:%s val:%s, layer:%s charstart: %s charend: %s", txt, fingerPrint, startPosition, size, val, layer, charStartOffset, charEndOffset);
    }

    private void copy(final PhraseHit pc) {
        this.fingerPrint = pc.fingerPrint;
        this.txt = pc.txt;
        this.startPosition = pc.startPosition;
        this.size = pc.size;
        this.val = pc.val;
        this.layer = pc.layer;
        this.charStartOffset = pc.charStartOffset;
        this.charEndOffset = pc.charEndOffset;
        this.actualValue = pc.actualValue;
        this.name = pc.name;

    }

    public void consume(long fingerPrint,
                        int startPosition,
                        int size,
                        String txt,
                        long val,
                        int layer,
                        int charStartOffset,
                        int charEndOffset) {
        this.fingerPrint = fingerPrint;
        this.txt = txt;
        this.startPosition = startPosition;
        this.size = size;
        this.val = val;
        this.layer = layer;
        this.charStartOffset = charStartOffset;
        this.charEndOffset = charEndOffset;
    }

    public int getEndIndex() {
        return startPosition + size;
    }

    public TermHitAdjacency testAdjacency(PhraseHit nextElem) {
        if (nextElem.startPosition >= startPosition && nextElem.startPosition <= getEndIndex()) {
            // we have some type of overlap
            if (nextElem.getEndIndex() <= (getEndIndex())) {
                return TermHitAdjacency.CONTAINED;
            }
            return TermHitAdjacency.OVERLAP;
        } else {
            return TermHitAdjacency.NOOVERLAP;
        }
    }
}


