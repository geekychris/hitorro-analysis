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

import com.hitorro.analysis.cky.ling.PennTreeRenderer;
import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.analysis.cky.model.Grammar;
import com.hitorro.analysis.cky.model.GrammarSingletonMapper;
import com.hitorro.analysis.cky.model.OpenNLPCKYLexicon;
import com.hitorro.analysis.cky.model.TreeAnnotations;
import com.hitorro.analysis.cky.parser.CKYParser;
import com.hitorro.util.commandandcontrol.CommandSession;
import com.hitorro.util.commandandcontrol.ano.ArgType;
import com.hitorro.util.commandandcontrol.ano.CommandDef;
import com.hitorro.util.commandandcontrol.ano.DebugArgAno;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.json.keys.StringProperty;

/**
 *
 */
public class CKYDriver {

    public static CKYParser parser = null;

    @CommandDef(command = "parser", description = "parser a sentence with a specific grammer ")
    public static final String cky(@DebugArgAno(keyName = "session",
            description = "session",
            defaultValue = "",
            argType = ArgType.Session) CommandSession session,
                                   @DebugArgAno(propType = StringProperty.class, description = "sentence", keyName = "sentence") String sentence) {
        if (parser == null) {
            OpenNLPCKYLexicon lex = new OpenNLPCKYLexicon("en");
            Grammar g = GrammarSingletonMapper.singleton.get(null);
            parser = new CKYParser(g, lex);
        }
        String parts[] = StringUtil.tokenizeFromSingleChar(sentence, " ");
        parser.parse(parts);
        Tree<String> tree = parser.getBacktrace("ROOT");
        if (tree == null) {
            tree = new Tree<String>("empty");
        }
        tree = TreeAnnotations.unAnnotateTree(tree);
        Tree<String> de = TreeAnnotations.deMarkovization(tree);
        String out = PennTreeRenderer.render(de);
        return out;
    }
}
