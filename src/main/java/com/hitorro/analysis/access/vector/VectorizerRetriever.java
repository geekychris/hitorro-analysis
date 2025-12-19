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
package com.hitorro.analysis.access.vector;

import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.language.Iso639Table;
import com.hitorro.obj.core.solr.RetrievalContext;
import com.hitorro.obj.core.solr.retrievalmodules.Retriever;
import com.hitorro.obj.core.solr.retrievalmodules.cluster.ClusteringRetreiver;
import com.hitorro.obj.core.solr.retrievalmodules.solr.SolrRetriever;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.json.keys.JsonProperty;
import com.hitorro.util.json.keys.StringProperty;

public class VectorizerRetriever implements Retriever {
    public static JsonProperty vectorizeKey = new JsonProperty("vectorize", "", null);
    public static StringProperty analyzerKey = new StringProperty("analyzer", "Tokenization chain to use", "htstandard, case");

    @Override
    public boolean getParticipate(final JVS query, final RetrievalContext context) {
        return vectorizeKey.apply(query) != null;
    }

    public boolean aggregates() {
        return true;
    }

    @Override
    public AbstractIterator<JVS> appendRetriever(final AbstractIterator<JVS> iter, final JVS query, final RetrievalContext context) {
        JsonNode vectorizerNode = vectorizeKey.apply(query);
        String title = ClusteringRetreiver.titleKey.apply(vectorizerNode);

        String body = ClusteringRetreiver.bodyKey.apply(vectorizerNode);

        String lang = SolrRetriever.langKey.apply(query);

        String analyzer = analyzerKey.apply(vectorizerNode);

        boolean includeOnRow = true;
        TermVectorsGenerator generator =
                new TermVectorsGenerator(analyzer,
                        Iso639Table.getInstance().getRow(lang));

        TermVectorIterator tvi =
                new TermVectorIterator(generator,
                        iter,
                        title,
                        body,
                        includeOnRow);
        context.addAggregate(tvi);
        return tvi;
    }
}
