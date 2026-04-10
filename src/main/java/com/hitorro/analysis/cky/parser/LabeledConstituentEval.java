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
package com.hitorro.analysis.cky.parser;

import com.hitorro.analysis.cky.ling.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LabeledConstituentEval<L> extends AbstractEval<L> {
    private Set<L> labelsToIgnore;
    private Set<L> punctuationTags;

    public LabeledConstituentEval(Set<L> labelsToIgnore, Set<L> punctuationTags) {
        this.labelsToIgnore = labelsToIgnore;
        this.punctuationTags = punctuationTags;
    }

    static <L> Tree<L> stripLeaves(Tree<L> tree) {
        if (tree.isLeaf()) {
            return null;
        }
        if (tree.isPreTerminal()) {
            return new Tree<L>(tree.getLabel());
        }
        List<Tree<L>> children = new ArrayList<Tree<L>>();
        for (Tree<L> child : tree.getChildren()) {
            Tree<L> stripped = stripLeaves(child);
            if (stripped != null) {
                children.add(stripped);
            }
        }
        return new Tree<L>(tree.getLabel(), children);
    }

    Set<Object> makeObjects(Tree<L> tree) {
        Tree<L> noLeafTree = stripLeaves(tree);
        Set<Object> set = new HashSet<Object>();
        addConstituents(noLeafTree, set, 0);
        return set;
    }

    private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
        if (tree.isLeaf()) {
            if (punctuationTags.contains(tree.getLabel())) {
                return 0;
            } else {
                return 1;
            }
        }
        int end = start;
        for (Tree<L> child : tree.getChildren()) {
            int childSpan = addConstituents(child, set, end);
            end += childSpan;
        }
        L label = tree.getLabel();
        if (!labelsToIgnore.contains(label)) {
            set.add(new LabeledConstituent<L>(label, start, end));
        }
        return end - start;
    }

}
