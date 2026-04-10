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
package com.hitorro.analysis.cky;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.hitorro.analysis.cky.ling.PennTreeRenderer;
import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.analysis.cky.model.Grammar;
import com.hitorro.analysis.cky.model.GrammarSingletonMapper;
import com.hitorro.analysis.cky.model.OpenNLPCKYLexicon;
import com.hitorro.analysis.cky.model.TreeAnnotations;
import com.hitorro.analysis.cky.parser.CKYParser;
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.StringUtil;


public class CKYMap extends BaseMapper<JsonValueSource, JsonValueSource> {
    private CKYParser parser = null;
    private String arr[][];

    public CKYMap(String arr[][]) {
        OpenNLPCKYLexicon lex = new OpenNLPCKYLexicon("en");
        Grammar g = GrammarSingletonMapper.singleton.get(null);
        parser = new CKYParser(g, lex);
        this.arr = arr;
    }

    public boolean isThreadSafe() {
        return false;
    }

    public BaseMapper getCopy() {
        return new CKYMap(arr);
    }

    public Class inputType() {
        return JsonValueSource.class;
    }

    public Class outputType() {
        return JsonValueSource.class;
    }

    @Override
    public JsonValueSource apply(final JsonValueSource e) {
        for (String a[] : arr) {
            getParse(e, a[0], a[1]);
        }
        return e;
    }

    private void getParse(final JsonValueSource e, String from, String to) {
        JsonNode f = (JsonNode) e.getValue(e, from);
        if (f == null) {
            return;
        }
        if (f.isArray()) {
            ArrayNode fromNode = (ArrayNode) f;
            if (f.size() > 0) {
                ArrayNode toNode = JsonNodeFactory.instance.arrayNode();
                e.setValue(e, to, toNode);
                getParse(fromNode, toNode);
            }
        } else {
            e.setValue(e, to, getParse(f.textValue()));
        }
    }

    private void getParse(ArrayNode nodeFrom, ArrayNode nodeTo) {
        int size = nodeFrom.size();
        for (int i = 0; i < size; i++) {
            String parse = getParse(nodeFrom.get(i).textValue());
            if (StringUtil.nullOrEmptyString(parse)) {
                return;
            }
            nodeTo.add(parse);
        }
    }

    private String getParse(String sentence) {
        String parts[] = StringUtil.tokenizeFromSingleChar(sentence, " ");
        if (parts.length > 20) {
            return null;
        }
        parser.parse(parts);
        Tree<String> tree = parser.getBacktrace("ROOT");
        if (tree == null) {
            tree = new Tree<String>("empty");
        }
        tree = TreeAnnotations.unAnnotateTree(tree);
        Tree<String> de = TreeAnnotations.deMarkovization(tree);
        return PennTreeRenderer.render(de);
    }
}

