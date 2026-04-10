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
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.language.IsoLanguage;
import com.hitorro.obj.core.GenericAnalyzer;
import com.hitorro.obj.core.GenericTokenizer2HashlongMapper;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.longword.WordBits;
import com.hitorro.util.core.map.FPHashBaseMap;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.json.keys.propaccess.PropaccessError;

import java.util.List;


public class TokenizingFPHashBaseMap extends FPHashBaseMap {
    private String analyzerString;
    private IsoLanguage lang;
    private GenericAnalyzer analyzer;
    private GenericTokenizer2HashlongMapper mapper;
    private FPHashDict dict;

    public TokenizingFPHashBaseMap(String analyzerString, IsoLanguage lang, WordBits wb, int initialSize, int layer) {
        this.analyzerString = analyzerString;
        this.lang = lang;
        dict = new FPHashDict(this, layer);
        mapper = new GenericTokenizer2HashlongMapper(analyzerString, lang);
        analyzer = new GenericAnalyzer(StringUtil.strcat(analyzerString, ",emitphrase"), lang, GenericAnalyzer.Mode.Index);
        init(layer, wb, initialSize, mapper, true);
    }

    public String getAnalyzerString() {
        return analyzerString;
    }

    public IsoLanguage getLang() {
        return lang;
    }

    public FPHashDict getFPHashDict() {
        return dict;
    }

    public GenericAnalyzer getAnalyzer() {
        return analyzer;
    }

    public TokenizingFPHashBaseMap setPhrases(List<GenericKeyValue<String, Integer>> tokens, String wordBitName) {
        JVS jvs = new JVS();
        for (GenericKeyValue<String, Integer> tok : tokens) {
            String phrase = tok.getKey();
            String v = tok.getValue().toString();
            try {
                jvs.set(wordBitName, v);
            } catch (PropaccessError propaccessError) {

            }
            updateRecord(jvs, phrase, layer);
        }
        return this;
    }
}
