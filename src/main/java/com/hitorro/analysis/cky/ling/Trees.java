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
package com.hitorro.analysis.cky.ling;

import com.hitorro.util.core.opers.HTPredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tools for displaying, reading, and modifying trees.
 *
 * @author Dan Klein
 * @author Paul Baumstarck (added GENIA and BioIE readers 2008.4)
 */
public class Trees {
    private static int tokCount;

    public static synchronized int getTokId() {
        return tokCount++;
    }

    /**
     * Splices out all nodes which test the provided filter.
     *
     * @param tree
     * @param filter
     */
    public static <L> Tree<L> spliceNodes(Tree<L> tree, HTPredicate<L> filter) {
        List<Tree<L>> rootList = spliceNodesHelper(tree, filter);
        if (rootList.size() > 1) {
            throw new IllegalArgumentException(
                    "spliceNodes: no unique root after splicing");
        }
        if (rootList.size() < 1) {
            return null;
        }
        return rootList.get(0);
    }

    private static <L> List<Tree<L>> spliceNodesHelper(Tree<L> tree,
                                                       HTPredicate<L> filter) {
        List<Tree<L>> splicedChildren = new ArrayList<Tree<L>>();
        for (Tree<L> child : tree.getChildren()) {
            List<Tree<L>> splicedChildList = spliceNodesHelper(child, filter);
            splicedChildren.addAll(splicedChildList);
        }
        if (filter.test(tree.getLabel())) {
            return splicedChildren;
        }
        return Collections.singletonList(new Tree<L>(tree.getLabel(),
                splicedChildren));
    }

    private static <L> Tree<L> pruneNodesHelper(Tree<L> tree, HTPredicate<L> filter) {
        if (filter.test(tree.getLabel())) {
            return null;
        }
        List<Tree<L>> prunedChildren = new ArrayList<Tree<L>>();
        for (Tree<L> child : tree.getChildren()) {
            Tree<L> prunedChild = pruneNodesHelper(child, filter);
            if (prunedChild != null) {
                prunedChildren.add(prunedChild);
            }
        }
        if (prunedChildren.isEmpty() && !tree.isLeaf()) {
            return null;
        }
        return new Tree<L>(tree.getLabel(), prunedChildren);
    }

}
