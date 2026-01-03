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

import com.hitorro.basetext.dfindex.FPHashDict;
import com.hitorro.language.IsoLanguage;
import com.hitorro.obj.core.FilterContext;
import com.hitorro.obj.core.GenericAnalyzer;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.longword.WordBits;
import com.hitorro.util.core.map.FPHashBaseMapInterface;
import com.hitorro.util.io.ResetableCharArrayReader;
import opennlp.tools.util.Span;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;
import java.util.List;

/**
 * Score a line by how many phrases test.  Interceptor could be replaced to support things like sentiment entity, affinity pairs.
 */
public abstract class BasicScorer<BS extends BasicScorerSentenceWrapper, BE extends BaseEmitter> implements SentenceScorer<BS, BE> {
    protected ResetableCharArrayReader reader = new ResetableCharArrayReader(new char[0]);
    protected FPHashBaseMapInterface map;
    protected FPHashDict dict;
    protected BE emitter;
    protected FilterContext fc;
    protected WordBits wb;
    protected GenericAnalyzer analyzer;
    protected int sentenceNumber = 0;

    // slows down the emitter by
    protected boolean recordStrings = true;


    public BasicScorer() {

    }

    /**
     * Constructor for simple case where we just want to identify
     *
     * @param map
     */
    public BasicScorer(GenericAnalyzer analyzer, WordBits wb, FPHashBaseMapInterface map) {
        init(map, wb, analyzer);
    }

    public BasicScorer(String analyzerString, IsoLanguage lang, List<GenericKeyValue<String, Integer>> tokens) {
        WordBits wb = new WordBits();
        wb.add("value", 32);
        TokenizingFPHashBaseMap mapTemp = new TokenizingFPHashBaseMap(analyzerString, lang, wb, tokens.size(), 0);
        analyzer = mapTemp.getAnalyzer();
        // initialize for simple GKV list
        mapTemp.setPhrases(tokens, "value");
        init(mapTemp, wb, analyzer);
    }

    public abstract BE getNewBaseEmitter();

    public void reset() {
        emitter.reset();
    }

    public WordBits getWordBits() {
        return wb;
    }

    public void init(FPHashBaseMapInterface mapIn, WordBits wb, GenericAnalyzer analyzer) {
        this.analyzer = analyzer;
        this.map = mapIn;
        this.wb = wb;
        dict = new FPHashDict(map, 0);
        emitter = getNewBaseEmitter();
        fc = new FilterContext();
        fc.fpPhraseContext = emitter;
        fc.baseIndex = dict;
        analyzer.setFilterContext(fc);
    }


    public boolean needsCharBuffer() {
        return true;
    }

    @Override
    public BS visit(final String content, char array[], final Span span, final int sentenceNumber) {
        reader.set(array, span.getStart(), span.getEnd() - span.getStart());
        TokenStream ts = analyzer.tokenStream("body", reader);
		try {
			ts.reset();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int count = 0;
        try {
            while (ts.incrementToken()) {
                count++;
            }
            fc.fpPhraseContext.end();
            ts.close();
        } catch (IOException e) {
            return null;
        }
        if (emitter.participate()) {
            BS bssw = getWrapper();
            bssw.sentenceOrder = sentenceNumber;
            bssw.consume(emitter.getScore(), count, span, content, emitter);
            return bssw;
        }
        return null;
    }


}

