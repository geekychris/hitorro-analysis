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
package com.hitorro.analysis.rdf;


import org.apache.commons.collections15.Transformer;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;

/**
 * @author pldms
 */
public class Transformers {

    public final static NodeT NODE = new NodeT();
    public final static EdgeT EDGE = new EdgeT();

    private final static String toString(Resource resource) {
        if (resource.isAnon()) {
            return "[]";
        }
        PrefixMapping pmap = resource.getModel();
        String qname = pmap.qnameFor(resource.getURI());
        if (qname != null) {
            return qname;
        }
        return "<" + resource.getURI() + ">";
    }

    public static class NodeT implements Transformer<RDFNode, String> {

        public String transform(RDFNode input) {
            if (input.isLiteral()) {
                return input.toString();
            } else {
                return Transformers.toString((Resource) input);
            }
        }

    }

    public static class EdgeT implements Transformer<Statement, String> {

        public String transform(Statement input) {
            return Transformers.toString(input.getPredicate());
        }

    }
}
