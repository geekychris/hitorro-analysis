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

import gnu.trove.map.hash.TIntObjectHashMap;
import com.hitorro.analysis.sentenceenumeration.collectingscorer.CollectingPhraseScorer;
import com.hitorro.analysis.sentenceenumeration.collectingscorer.CollectingSentenceWrapper;
import com.hitorro.analysis.sentenceenumeration.collectingscorer.PhraseCollection;
import com.hitorro.analysis.sentenceenumeration.collectingscorer.PhraseHitLogicalLongOper;
import com.hitorro.analysis.sentiment.penn.CountSink;
import com.hitorro.analysis.sentiment.penn.SentimentUtil;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.obj.core.GenericAnalyzer;
import com.hitorro.util.Counter;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.ListUtil;
import com.hitorro.util.core.iterator.Mapper;
import com.hitorro.util.core.longword.NamedBitsOfLong;
import com.hitorro.util.core.longword.WordBits;
import com.hitorro.util.core.longword.opers.BitSetOperator;
import com.hitorro.util.core.map.CascadingFPHashBaseMap;
import com.hitorro.util.core.map.FPHashBaseMap;
import com.hitorro.util.core.map.FPHashBaseMapInterface;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SEBuilder {

    public static Mapper<IsoLanguage, List<GenericKeyValue<String, Integer>>> sentimentFactory = new Mapper<IsoLanguage, List<GenericKeyValue<String, Integer>>>() {
        public List<GenericKeyValue<String, Integer>> apply(IsoLanguage lang) {
            List<GenericKeyValue<String, Integer>> sent = new ArrayList();

            CountSink sink = new CountSink();
            try {
                SentimentUtil.getRawValues().sink(sink);
            } catch (IOException e) {
                return null;
            }
            Counter<String> c = sink.getCounters().getCounter("word1");
            for (String word : c.keySet()) {
                sent.add(new GenericKeyValue<String, Integer>(word, 1));
            }
            return sent;
        }
    };
    protected WordBits wb = new WordBits();
    protected String analyzerString = "htstandard,case";
    protected IsoLanguage lang = Iso639Table.english;
    protected List<FPHashBaseMap> mapList = new ArrayList();
    protected TIntObjectHashMap<FPHashBaseMap> layer2map = new TIntObjectHashMap();
    protected FPHashBaseMap currMap = null;
    protected int maxHits = 100;
    protected GenericAnalyzer analyzer;
    protected RawSentenceSelectionContext ssc;
    private CollectingPhraseScorer bs;
    private Map<String, PhraseHitLogicalLongOper> wordbit2phasehit = new HashMap();

    public static void test() {
        SEBuilder s = new SEBuilder();
        s.addWordBits("sent", 1).
                addPhrases(sentimentFactory.apply(Iso639Table.english), "sent").
                createScorer();

        SEBuilder child =
                s.subLayer().
                        addWordBits("keywords", 1).
                        createScorer().
                        addPhrases(1, "keywords", "mega", "fun", "poop doctor", "smoking");

        child.parse("I love to have mega fun listening to poop doctor hate.  When in paris I hate the smoking.", "sent", "keywords");
    }

    /**
     * Add a new layer that does not polute the previous layer allowing the
     * use of a larger substrate (such as sentiment) while allowing for a new layer on top (such as keywords from a search).
     *
     * @return
     */
    public SEBuilder subLayer() {
        SEBuilder child = new SEBuilder();
        child.analyzerString = analyzerString;
        child.mapList = new ArrayList<>();
        child.mapList.addAll(mapList);
        child.currMap = currMap;
        child.maxHits = maxHits;
        child.analyzer = analyzer;
        child.createMap();
        child.refreshLayer2Map();
        return child;
    }

    private void refreshLayer2Map() {
        layer2map = new TIntObjectHashMap();
        for (FPHashBaseMap m : mapList) {
            layer2map.put(m.getLayer(), m);
        }
    }

    public SEBuilder setAnalyzer(String analyzer) {
        this.analyzerString = analyzer;
        return this;
    }

    public SEBuilder setMaxHits(int hits) {
        this.maxHits = hits;
        return this;
    }

    public SEBuilder setLang(IsoLanguage lang) {
        this.lang = lang;
        return this;
    }

    public SEBuilder setLang(String lang) {
        return setLang(Iso639Table.getInstance().getRow(lang));
    }

    public SEBuilder createScorer() {

        FPHashBaseMapInterface maps = getCombinedMaps();
        if (maps == null) {
            // fail fast.
            return null;
        }
        bs = new CollectingPhraseScorer(
                analyzer,
                wb,
                maps,
                maxHits);
        return this;
    }

    public RawSentenceSelectionContext getSentenceContext() {
        if (ssc == null) {
            ssc = new RawSentenceSelectionContext();
        }

        return ssc;
    }

    public FPHashBaseMapInterface getCombinedMaps() {
        if (mapList.size() == 0) {
            return null;
        }
        if (mapList.size() == 1) {
            return mapList.get(0);
        }
        return new CascadingFPHashBaseMap(
                mapList.toArray(new FPHashBaseMap[mapList.size()]));
    }

    public SEBuilder addWordBits(String name, int width) {
        wb.add(name, width);
        return this;
    }

    public SEBuilder addPhrases(int value, String wordBitName, String... values) {
        return addPhrases(ListUtil.addGKVWithDefaultValue(values,
                        Integer.valueOf(value),
                        new ArrayList<GenericKeyValue<String, Integer>>()),
                wordBitName);
    }

    /**
     * Given a set of tokens and their values that fit into a wordbit window hash the tokens and add to the
     * map;
     *
     * @param tokens
     * @param wordBitName
     * @return
     */
    public SEBuilder addPhrases(List<GenericKeyValue<String, Integer>> tokens, String wordBitName) {
        if (currMap == null) {
            createMap();
        }
        currMap.setPhrases(tokens, wordBitName);
        return this;
    }

    public FPHashBaseMap createMap() {
        // initialize for simple GKV list
        int layerNumber = mapList.size();
        TokenizingFPHashBaseMap layer =
                new TokenizingFPHashBaseMap(analyzerString,
                        Iso639Table.english,
                        wb,
                        100,
                        layerNumber);

        if (mapList.size() == 0) {
            analyzer = layer.getAnalyzer();
        }
        currMap = layer;
        layer2map.put(currMap.getLayer(), currMap);
        mapList.add(layer);
        return layer;
    }

    public PhraseHitLogicalLongOper getHitOperator(String wordBitsName) {
        PhraseHitLogicalLongOper ret = wordbit2phasehit.get(wordBitsName);
        if (ret == null) {
            NamedBitsOfLong nbol = wb.get(wordBitsName);
            ret = new PhraseHitLogicalLongOper(
                    new BitSetOperator(nbol));
            wordbit2phasehit.put(wordBitsName, ret);
        }

        return ret;
    }

    public void parse(String text, String... wordBitsToFind) {
        parse(text, null, wordBitsToFind);
    }

    public List<CollectingSentenceWrapper> generate(String text, Span spans[], int maxChars, String... wordbits) {
        bs.reset();
        RawSentenceSelectionContext ssc = getSentenceContext();
        ssc.set(text, spans, lang, bs);
        if (ssc.parse()) {
            if (maxChars > 0) {
                ssc.markScored(maxChars);
            }

            List<CollectingSentenceWrapper> l = ssc.getSentences();
            for (CollectingSentenceWrapper sw : l) {
                PhraseCollection pc = sw.getPhraseCollection();
                pc.decorateWordBits(this.layer2map);

            }

            return ssc.getSentences();
        }
        return null;
    }

    public void parse(String text, Span spans[], String... wordBitsToFind) {
        List<CollectingSentenceWrapper> l = generate(text, spans, 0);
        if (l != null) {
            for (CollectingSentenceWrapper sw : l) {
                PhraseCollection pc = sw.getPhraseCollection();
                pc.decorateWordBits(this.layer2map);

                for (String w : wordBitsToFind) {
                    PhraseHitLogicalLongOper oper = getHitOperator(w);
                    int index = pc.indeOf(0, oper);
                    if (index != -1) {
                        Console.println("found of %s", pc.getPhraseHitCopy(index));
                    }

                }

                Console.println("%s %s", sw.getScore(), sw.getText());
            }
        }
    }
}


