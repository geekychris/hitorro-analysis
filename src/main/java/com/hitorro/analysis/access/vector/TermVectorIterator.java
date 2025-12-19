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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.obj.core.solr.RetrievalContext;
import com.hitorro.obj.core.solr.retrievalmodules.RetrievalAggregate;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.json.keys.propaccess.Propaccess;

import java.io.IOException;

public class TermVectorIterator extends AbstractIterator<JVS> implements RetrievalAggregate {
    private AbstractIterator<JVS> iterIn;

    private ArrayNode an = JsonNodeFactory.instance.arrayNode();
    private TermVectorsGenerator generator;
    private ArrayNode rows = JsonNodeFactory.instance.arrayNode();
    private boolean includeOnRow;
    private Propaccess titleProp;
    private Propaccess bodyProp;
    private Propaccess vecProp = new Propaccess("termvector");

    public TermVectorIterator(TermVectorsGenerator generator,
                              AbstractIterator<JVS> iterIn,
                              String titleIn,
                              String bodyIn,
                              boolean includeOnRow) {
        this.iterIn = iterIn;
        this.includeOnRow = includeOnRow;
        this.generator = generator;
        this.titleProp = new Propaccess(titleIn);
        this.bodyProp = new Propaccess(bodyIn);
    }

    @Override
    public void close() throws IOException {
        try {
            AbstractIterator.attemptClose(iterIn);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean hasNext() {
        return iterIn.hasNext();
    }

    @Override
    public JVS next() {
        JVS vsIn = iterIn.next();
        JVS vs = vsIn.getDoc();

        String tVal = vs.getString(titleProp);
        String bVal = vs.getString(bodyProp);
        JsonNode row = null;
        try {
            row = generator.add(tVal, bVal);
            if (includeOnRow && row != null) {
                vsIn.set(vecProp, row);
            }
        } catch (IOException e) {
            return null;
        }
        if (row != null) {
            rows.add(row);
        }

        return vsIn;
    }

    @Override
    public void remove() {
        iterIn.remove();
    }

    @Override
    public JVS getAggregate(final RetrievalContext context) {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.put("type", "core_termvector");
        root.put("rows", rows);
        generator.setInfo(root);
        return new JVS(root);
    }
}

