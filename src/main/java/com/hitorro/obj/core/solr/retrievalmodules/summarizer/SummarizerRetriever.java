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
package com.hitorro.obj.core.solr.retrievalmodules.summarizer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitorro.analysis.sentenceenumeration.SEBuilder;
import com.hitorro.analysis.sentenceenumeration.collectingscorer.CollectingSentenceWrapper;
import com.hitorro.analysis.sentenceenumeration.collectingscorer.PhraseCollection;
import com.hitorro.analysis.sentenceenumeration.collectingscorer.PhraseHit;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.jsontypesystem.dynamic.SentenceSegmenter;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.obj.core.solr.RetrievalContext;
import com.hitorro.obj.core.solr.retrievalmodules.Retriever;
import com.hitorro.obj.core.solr.retrievalmodules.cluster.ClusteringRetreiver;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.ListUtil;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.Mapper;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.json.keys.CollectionProperty;
import com.hitorro.util.json.keys.IntegerProperty;
import com.hitorro.util.json.keys.JsonProperty;
import com.hitorro.util.json.keys.StringProperty;
import com.hitorro.util.json.keys.propaccess.Propaccess;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.List;

public class SummarizerRetriever implements Retriever {
    public static JsonProperty summarizerKey = new JsonProperty("summarize", "", null);
    public static IntegerProperty sizeCount = new IntegerProperty("size", "", 130);
    public static StringProperty analyzerKey = new StringProperty("analyzer", "Tokenization chain to use", "htstandard, case");

    public static CollectionProperty<String> queriesKey = CollectionProperty.getStringCollection("queries", "");

    @Override
    public boolean getParticipate(final JVS query, final RetrievalContext context) {
        return summarizerKey.apply(query) != null;
    }

    @Override
    public AbstractIterator<JVS> appendRetriever(final AbstractIterator<JVS> iter, final JVS query, final RetrievalContext context) {
        JsonNode node = summarizerKey.apply(query);

        return iter.map(
                new SummarizerMapper(node,
                        Iso639Table.getInstance().getRow(
                                context.getLang())));
    }
}

class SummarizerMapper implements Mapper<JVS, JVS> {
    int size;
    String title;
    String body;
    Propaccess titleP;
    Propaccess bodyP;
    Propaccess titlePSpan;
    Propaccess bodyPSpan;
    Propaccess titleTargetP;
    Propaccess bodyTargetP;
    List<String> queries;
    List<GenericKeyValue<String, Integer>> queriesWithWeight;
    private IsoLanguage lang;
    private int maxChars = 100;

    private SEBuilder child;

    public SummarizerMapper(JsonNode node, IsoLanguage lang) {
        this.lang = lang;
        size = SummarizerRetriever.sizeCount.apply(node);
        title = ClusteringRetreiver.titleKey.apply(node);

        body = ClusteringRetreiver.bodyKey.apply(node);
        titleP = new Propaccess(title);
        bodyP = new Propaccess(body);
        titlePSpan = titleP.clone().replaceLeaf("segmented_span");
        bodyPSpan = bodyP.clone().replaceLeaf("segmented_span");
        titleTargetP = titleP.clone().replaceLeaf("summarizer_span");
        bodyTargetP = bodyP.clone().replaceLeaf("summarizer_span");
        queries = SummarizerRetriever.queriesKey.apply(node);
        queriesWithWeight =
                ListUtil.addGKVWithDefaultValue(queries,
                        1,
                        new ArrayList());

        List<GenericKeyValue<String, Integer>> list = SEBuilder.sentimentFactory.apply(lang);
        SEBuilder s = new SEBuilder();
        s.addWordBits("sent", 1).
                addPhrases(list, "sent").
                createScorer();

        child =
                s.subLayer().
                        addWordBits("keywords", 1).
                        createScorer().
                        addPhrases(queriesWithWeight, "keywords");

    }

    @Override
    public JVS apply(final JVS jvs) {
        JVS doc = jvs.getDoc();

        doIt(titleP, titlePSpan, titleTargetP, doc);

        doIt(bodyP, bodyPSpan, bodyTargetP, doc);

        return jvs;
    }

    private void doIt(Propaccess field, Propaccess spanField, Propaccess target, JVS doc) {
        String text = doc.getString(field);
        if (StringUtil.nullOrEmptyString(text)) {
            return;
        }
        JsonNode span = doc.get(spanField);
        Span[] spans = SentenceSegmenter.getSpans(span, text);
        List<CollectingSentenceWrapper> l = child.generate(text, spans, maxChars);

        if (!ListUtil.nullOrEmpty(l)) {
            ArrayNode v = JsonNodeFactory.instance.arrayNode();
            for (CollectingSentenceWrapper w : l) {
                ObjectNode on = JsonNodeFactory.instance.objectNode();

                on.put("inc_length", w.getIncludeLength());
                PhraseCollection pc = w.getPhraseCollection();
                if (pc.getSize() > 0) {
                    ArrayNode hits = JsonNodeFactory.instance.arrayNode();
                    on.put("hits", hits);
                    for (int i = 0; i < pc.getSize(); i++) {
                        PhraseHit ph = pc.getPhraseHitCopy(i);
                        ObjectNode h = JsonNodeFactory.instance.objectNode();
                        h.put("v", ph.actualValue);
                        h.put("s", ph.startPosition);
                        h.put("e", ph.getEndIndex());
                        h.put("cs", ph.charStartOffset);
                        h.put("ce", ph.charEndOffset);
                        h.put("t", ph.txt);
                        h.put("v", ph.actualValue);
                        h.put("n", ph.name);
                        hits.add(h);
                    }
                }

                v.add(on);
            }
            doc.set(target, v);
        }
    }
}
