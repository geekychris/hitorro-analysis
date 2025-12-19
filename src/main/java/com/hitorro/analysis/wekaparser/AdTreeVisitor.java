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
package com.hitorro.analysis.wekaparser;

import gnu.trove.map.hash.TIntObjectHashMap;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.string.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


/*

: -0.931
|  (1)MENTION_TEXT_PHRASE_DICT_SPAM_COUNT < 4.5: -0.413
|  |  (4)MENTION_TEXT_PHRASE_DICT_SPAM_COUNT < 0.5: -0.147
|  |  (4)MENTION_TEXT_PHRASE_DICT_SPAM_COUNT >= 0.5: 0.386
|  |  (5)MENTION_TITLE_WORD_COUNT < 9.5: -0.143
|  |  (5)MENTION_TITLE_WORD_COUNT >= 9.5: 0.355
|  (1)MENTION_TEXT_PHRASE_DICT_SPAM_COUNT >= 4.5: 1.336
|  |  (3)MENTION_TEXT_PHRASE_DICT_SPAM_UNIQCOUNT < 20.5: -0.402
|  |  |  (10)MENTION_TEXT_PHRASE_BIGRAM_UNIQCOUNT < 207: 1.085
|  |  |  (10)MENTION_TEXT_PHRASE_BIGRAM_UNIQCOUNT >= 207: -0.222
|  |  (3)MENTION_TEXT_PHRASE_DICT_SPAM_UNIQCOUNT >= 20.5: 1.453


 */
public class AdTreeVisitor extends TreeVisitor<ADNode> {
    private double startValue;
    private TIntObjectHashMap<ADNode> map = new TIntObjectHashMap();

    private List<ADNode> rootNodes = new ArrayList();
    private ADNode root = new ADNode(0, 0, "", "", "0.0", "0.0");
    private int lastDepth = 0;
    private Stack<ADNode> stack = new Stack();

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ADNode node : rootNodes) {
            sb.append(node.toString());
        }
        return sb.toString();
    }

    public ADNode getRoot() {
        return root;
    }

    public void visit(List<String> buff) {
        stack.push(root);
        Iterator<String> iter = buff.iterator();
        while (iter.hasNext()) {
            String row = iter.next();
            if (row.indexOf("|") != -1) {
                addRow(row);
            } else if (row.startsWith(":")) {
                // root value
                startValue = Double.parseDouble(row.substring(1).trim());
            }
        }
        if (rootNodes.size() > 0) {
            root = rootNodes.get(0);
        }
    }


    private void addRow(String row) {
        int pStart = row.indexOf("(");
        int pEnd = row.indexOf(")");
        if (pEnd == -1) {
            return;
        }
        String num = row.substring(pStart + 1, pEnd);
        int index = Integer.parseInt(num);
        if (index == 3) {
            Console.println();
        }
        int depth = StringUtil.count(row, "|");
        String rest[] = StringUtil.tokenizeFromSingleChar(row.substring(pEnd + 1), " ", true);
        ADNode tree = map.get(index);

        String val = rest[2].substring(0, rest[2].length() - 1);
        adjustToDepth(depth);
        if (tree == null) {

            //ADNode (int depth, String feature, String operator, String value)

            tree = new ADNode(index, depth, rest[0], rest[1], val, rest[3]);
            ADNode parent = stack.peek();

            parent.addNode(tree);

            map.put(index, tree);

        } else {
            tree.switchToRight();
            tree.falseBoost = Double.parseDouble(rest[3]);
        }
        if (lastDepth < depth) {
            Console.println("adding %s depth %s", tree, depth);
            stack.push(tree);
        } else if (lastDepth > depth) {
            Console.println("popping %s depth %s", tree, depth);
            stack.pop();
        } else {
            Console.println("Rotating position");
            stack.pop();
            stack.push(tree);
        }
        lastDepth = depth;
    }

    private void adjustToDepth(int depth) {
        while (stack.size() > depth) {
            stack.pop();
            lastDepth--;
        }
    }

    public String getImplements() {
        return "weka.classifiers.trees.ADTree";
    }
}

