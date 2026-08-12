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

import gnu.trove.map.hash.TIntObjectHashMap;
import com.hitorro.util.core.longword.NamedBitsOfLong;
import com.hitorro.util.core.longword.WordBits;
import com.hitorro.util.core.map.FPHashBaseMap;

import java.util.Arrays;
import java.util.Iterator;


public class PhraseCollection {
    private PhraseHit hits[];
    private int maxHits;
    private int hitIndex;
    private boolean sorted;

    public PhraseCollection(int maxHits) {
        this.maxHits = maxHits;
        hits = new PhraseHit[maxHits];
        for (int i = 0; i < maxHits; i++) {
            hits[i] = new PhraseHit();
        }
    }

    private PhraseCollection(PhraseCollection pcIn) {
        this.maxHits = pcIn.getSize();
        hits = new PhraseHit[maxHits];
        this.hitIndex = maxHits;
        for (int i = 0; i < maxHits; i++) {
            hits[i] = pcIn.getPhraseHitCopy(i);
        }
    }

    public int getSize() {
        return hitIndex;
    }

    public void decorateWordBits(TIntObjectHashMap<FPHashBaseMap> layer2map) {
        for (int i = 0; i < maxHits; i++) {
            PhraseHit ph = hits[i];
            FPHashBaseMap map = layer2map.get(ph.layer);
            WordBits wb = map.getWordBits(ph.layer);
            Iterator<NamedBitsOfLong> iter = wb.iterator();
            while (iter.hasNext()) {
                NamedBitsOfLong nl = iter.next();
                long l = nl.get(ph.val);
                if (l != 0) {
                    ph.actualValue = l;
                    ph.name = nl.getName();
                }
            }
        }
    }

    public PhraseHit getPhraseHitCopy(int index) {
        return new PhraseHit(hits[index]);
    }

    public void reset() {
        hitIndex = 0;
        sorted = false;
    }

    public void phraseMatch(long fingerPrint, int startPosition, int size, String txt, long val, int layer, int charStartOffset, int charEndOffset) {
        if (hitIndex >= maxHits) {
            // cant do any more hit max
            return;
        }
        hits[hitIndex++].consume(fingerPrint, startPosition, size, txt, val, layer, charStartOffset, charEndOffset);
    }

    /**
     * @return
     */
    public boolean hasOverlapping() {
        ensureSort();
        if (hitIndex == 1) {
            return false;
        }
        for (int i = 0; i < hitIndex - 1; i++) {

        }
        return false;

    }

    public PhraseCollection getCopy() {
        PhraseCollection copy = new PhraseCollection(this);
        return copy;
    }

    /**
     * scan from the starting index to the end of the hits looking for the first test to the
     *
     * @param startIndex
     * @param oper
     * @return
     */
    public int indeOf(int startIndex, java.util.function.Predicate<PhraseHit> oper) {
        return indeOf(startIndex, hitIndex, oper);
    }

    public int indeOf(int startIndex, int endIndex, java.util.function.Predicate<PhraseHit> oper) {
        if (startIndex < hitIndex) {
            for (int i = startIndex; i < endIndex; i++) {
                if (oper.test(hits[i])) {
                    return i;
                }
            }
        }
        return -1;

    }

    private void ensureSort() {
        if (hitIndex > 1 && sorted == false) {
            Arrays.sort(hits, 0, hitIndex - 1, TermHitComparator.comp);
        }
        sorted = true;
    }
}
