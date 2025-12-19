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

import opennlp.tools.util.Span;

/**
 * Sentence that has a non zero score
 */
public abstract class BaseSentenceWrapper<BE extends BaseEmitter> {
    int sentenceOrder;
    int includedLength = 0;
    private int wordLength;
    private float score;
    private Span span;
    private String content;

    public BaseSentenceWrapper() {
    }

    public void consume(final float score, final int wordLength, final Span span, final String content, BE be) {
        this.score = score;
        this.wordLength = wordLength;
        this.span = span;
        this.content = content;
    }

    public String getText() {
        return content.substring(span.getStart(), span.getEnd());
    }

    public int getWordLength() {
        return wordLength;
    }

    public Span getSpan() {
        return span;
    }

    public float getScore() {
        return score;
    }

    public int getIncludeLength() {
        return includedLength;
    }
}
