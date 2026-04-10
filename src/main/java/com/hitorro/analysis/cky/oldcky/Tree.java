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
package com.hitorro.analysis.cky.oldcky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Tree<L> {
    L label;
    List<Tree<L>> children;

    /* The leaf constructor. */
    public Tree(L label, List<Tree<L>> children) {
        this.label = label;
        this.children = children;
    }

    public Tree(L label) {
        this.label = label;
        this.children = Collections.emptyList();
    }

    private static <L> void appendYield(Tree<L> tree, List<L> yield) {
        if (tree.isLeaf()) {
            yield.add(tree.getLabel());
            return;
        }
        for (Tree<L> child : tree.getChildren()) {
            appendYield(child, yield);
        }
    }

    private static <L> void appendPreTerminalYield(Tree<L> tree,
                                                   List<L> yield) {
        if (tree.isPreTerminal()) {
            yield.add(tree.getLabel());
            return;
        }
        for (Tree<L> child : tree.getChildren()) {
            appendPreTerminalYield(child, yield);
        }
    }

    private static <L> void traversalHelper(Tree<L> tree, List<Tree<L>> traversal,
                                            boolean preOrder) {
        if (preOrder) {
            traversal.add(tree);
        }
        for (Tree<L> child : tree.getChildren()) {
            traversalHelper(child, traversal, preOrder);
        }
        if (!preOrder) {
            traversal.add(tree);
        }
    }

    private static <L> int toConstituentCollectionHelper(Tree<L> tree, int start,
                                                         List<Constituent<L>> constituents) {
        if (tree.isLeaf() || tree.isPreTerminal()) {
            return 1;
        }
        int span = 0;
        for (Tree<L> child : tree.getChildren()) {
            span += toConstituentCollectionHelper(child, start + span, constituents);
        }
        constituents.add(new Constituent<L>(tree.getLabel(), start, start + span));
        return span;
    }

    private static <L> Tree<L> deepCopy(Tree<L> tree) {
        List<Tree<L>> childrenCopies = new ArrayList<Tree<L>>();
        for (Tree<L> child : tree.getChildren()) {
            childrenCopies.add(deepCopy(child));
        }
        return new Tree<L>(tree.getLabel(), childrenCopies);
    }

    public List<Tree<L>> getChildren() {
        return children;
    }

    public void setChildren(List<Tree<L>> children) {
        this.children = children;
    }

    public L getLabel() {
        return label;
    }

    public void setLabel(L label) {
        this.label = label;
    }

    /* Returns true at the word(leaf) level of a tree */
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    /* Returns true level of non-terminals which are directly above
     * single words(leafs) */
    public boolean isPreTerminal() {
        return getChildren().size() == 1 && getChildren().get(0).isLeaf();
    }

    public boolean isPhrasal() {
        return !(isLeaf() || isPreTerminal());
    }

    /* Returns a list of words at the leafs of this tree gotten by
     * traversing from left to right */
    public List<L> getYield() {
        List<L> yield = new ArrayList<L>();
        appendYield(this, yield);
        return yield;
    }

    /* Returns a list of the preterminals gotten by traversing from left
     * to right.  This is effectively an POS tagging for the words that
     * tree represents. */
    public List<L> getPreTerminalYield() {
        List<L> yield = new ArrayList<L>();
        appendPreTerminalYield(this, yield);
        return yield;
    }

    /* Returns a list of the node values gotten by traversing in this
     * order: root, left subtree, right subtree */
    public List<Tree<L>> getPreOrderTraversal() {
        ArrayList<Tree<L>> traversal = new ArrayList<Tree<L>>();
        traversalHelper(this, traversal, true);
        return traversal;
    }

    /* Returns a list of the node values gotten by traversing in this
     * order: left subtree, right subtree, root */
    public List<Tree<L>> getPostOrderTraversal() {
        ArrayList<Tree<L>> traversal = new ArrayList<Tree<L>>();
        traversalHelper(this, traversal, false);
        return traversal;
    }

    /* Set the words at the leaves of a tree to the words from the
     * list */
    public void setWords(List<L> words) {
        setWordsHelper(words, 0);
    }

    private int setWordsHelper(List<L> words, int wordNum) {
        if (isLeaf()) {
            label = words.get(wordNum);
            return wordNum + 1;
        } else {
            for (Tree<L> child : getChildren()) {
                wordNum = child.setWordsHelper(words, wordNum);
            }
            return wordNum;
        }
    }

    public List<Tree<L>> toSubTreeList() {
        return getPreOrderTraversal();
    }

    /* Creates a list of all constituents in this tree  A constituent
     * is just a non-terminal label and that non-terminal covers in the
     * tree. */
    public List<Constituent<L>> toConstituentList() {
        List<Constituent<L>> constituentList = new ArrayList<Constituent<L>>();
        toConstituentCollectionHelper(this, 0, constituentList);
        return constituentList;
    }

    /* Returns a string representation of this tree using bracket
     * notation. */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringBuilder(sb);
        return sb.toString();
    }

    public void toStringBuilder(StringBuilder sb) {
        if (!isLeaf()) {
            sb.append('(');
        }
        if (getLabel() != null) {
            sb.append(getLabel());
        }
        if (!isLeaf()) {
            for (Tree<L> child : getChildren()) {
                sb.append(' ');
                child.toStringBuilder(sb);
            }
            sb.append(')');
        }
    }

    public Tree<L> deepCopy() {
        return deepCopy(this);
    }
}

