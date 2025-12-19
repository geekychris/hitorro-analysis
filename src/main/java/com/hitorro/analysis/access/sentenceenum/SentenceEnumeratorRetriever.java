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
package com.hitorro.analysis.access.sentenceenum;

import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.obj.core.solr.RetrievalContext;
import com.hitorro.obj.core.solr.retrievalmodules.Retriever;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.Mapper;
import com.hitorro.util.json.keys.JsonProperty;

/**
 * find in sentences any matching phrase from a phrase dictionary.
 * <p>
 * bonus points:
 * identify sentences with intersections of dictionaries such as emotion or sentiment and terms we are looking for such as an entity list.
 * Performed to n MLS strings.
 */
public class SentenceEnumeratorRetriever implements Retriever {
    public static JsonProperty sentEnumKey = new JsonProperty("sentenum", "", null);

    @Override
    public boolean getParticipate(final JVS query, final RetrievalContext context) {
        JsonNode node = sentEnumKey.apply(query);
        return node != null;
    }

    @Override
    public AbstractIterator<JVS> appendRetriever(final AbstractIterator<JVS> iter, final JVS query, final RetrievalContext context) {
        return iter.map(new SentenceEnumMapper());
    }
}

class SentenceEnumMapper implements Mapper<JVS, JVS> {

    @Override
    public JVS apply(final JVS jvs) {
        return jvs;
    }
}