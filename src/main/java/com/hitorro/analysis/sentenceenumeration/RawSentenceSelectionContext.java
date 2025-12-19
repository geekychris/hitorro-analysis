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
package com.hitorro.analysis.sentenceenumeration;

import com.hitorro.util.core.events.cache.PoolContainer;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.CollectionIterator;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Base container that will take a chunk of text, split it into sentences and then allow you to select a setPhrases of
 * sentences that are candidates for summarization.
 * <p/>
 * Approach: Provide raw text and language Split into sentences Score sentences using sentence scorer (abstract deligate
 * that can be swapped in).
 * <p/>
 * Once there are sentences that are scored, there are various ways one can select which sentences you want to use and
 * how much.  For example:
 * <p/>
 * Take the first n non zero scored sentences with a cap on m characters or m words
 */
public class RawSentenceSelectionContext<T extends BaseSentenceWrapper, BE extends BaseEmitter> implements com.hitorro.language.SentenceVisitor {
    public static Comparator<BaseSentenceWrapper> sortByRank = new Comparator<BaseSentenceWrapper>() {

        @Override
        public int compare(final BaseSentenceWrapper o1, final BaseSentenceWrapper o2) {
            if (o1.getScore() >= o2.getScore()) {
                return 1;
            }
            return 0;
        }
    };
    public static Comparator<BaseSentenceWrapper> sortByOrder = new Comparator<BaseSentenceWrapper>() {

        @Override
        public int compare(final BaseSentenceWrapper o1, final BaseSentenceWrapper o2) {
            if (o1.sentenceOrder < o2.sentenceOrder) {
                return 1;
            }
            return 0;
        }
    };
    private String rawText;
    private char[] rawBuff;
    private com.hitorro.language.IsoLanguage lang;
    private SentenceScorer<T, BE> scorer;
    private boolean computed = false;
    private List<BaseSentenceWrapper> scoredSentences = new ArrayList();
    private Span spans[];

    public RawSentenceSelectionContext<T, BE> set(String text, Span spans[], com.hitorro.language.IsoLanguage lang, SentenceScorer scorer) {
        this.spans = spans;
        rawText = text;
        if (scorer.needsCharBuffer()) {
            if (rawBuff == null || rawBuff.length < text.length()) {
                rawBuff = new char[text.length()];
            }
            // copy
            rawText.getChars(0, rawText.length(), rawBuff, 0);
        }

        this.lang = lang;
        this.scorer = scorer;
        computed = false;
        scoredSentences.clear();
        return this;
    }

    public List<BaseSentenceWrapper> getSentences() {
        return scoredSentences;
    }


    /**
     * dumb sentence selector that takes the most important (by score) sentences) and then
     * marks them with how many characters they contribute.  The last sentence in sentence order is the
     * one that gets chopped short.
     *
     * @param length
     */
    public void markScored(int length) {
        this.scoredSentences.sort(sortByRank);
        // grab first n chars
        List<BaseSentenceWrapper> theInCrowd = new ArrayList();
        int accum = 0;
        for (BaseSentenceWrapper bsw : scoredSentences) {
            if (bsw.getScore() > 0.0) {
                int size = bsw.getWordLength();
                theInCrowd.add(bsw);
                accum += size;
                if (accum > length) {
                    break;
                }
            }
        }
        theInCrowd.sort(sortByOrder);
        accum = 0;
        for (BaseSentenceWrapper bsw : scoredSentences) {
            int curr = bsw.getWordLength();
            bsw.includedLength = Math.min(length - accum, curr);
            accum += bsw.includedLength;
        }

        this.scoredSentences.sort(sortByOrder);
    }

    public AbstractIterator<BaseSentenceWrapper> getIterator() {
        return new CollectionIterator<BaseSentenceWrapper>(scoredSentences);
    }

    public boolean parse() {
        if (computed) {
            return true;
        }
        if (spans != null) {
            com.hitorro.language.Sentences s = new com.hitorro.language.Sentences(null, rawText, spans);
            s.visitSentences(this);
            computed = true;
            return true;
        }
        // dont have spans, go get them via parse
        PoolContainer<com.hitorro.language.IsoLanguage, com.hitorro.language.SentenceSegmenter> pool = com.hitorro.language.SentenceDetectorSingleton.singleton.get(lang);
        if (pool == null) {
            return false;
        }
        com.hitorro.language.SentenceSegmenter ss = null;
        try {
            ss = pool.get();
            com.hitorro.language.Sentences s = ss.getSentenceOffsets(rawText);
            s.visitSentences(this);
        } finally {
            pool.returnIt(ss);
        }
        computed = true;
        return true;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean visit(final String content, final Span span, final int sentenceNumber) {
        T sw = scorer.visit(content, rawBuff, span, sentenceNumber);
        if (sw != null) {
            this.scoredSentences.add(sw);
        }
        return true;
    }

    @Override
    public boolean end() {
        return true;
    }

    @Override
    public boolean finishUse() {
        return true;
    }
}

