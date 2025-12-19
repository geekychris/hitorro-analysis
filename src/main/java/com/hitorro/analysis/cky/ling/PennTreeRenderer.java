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

import com.hitorro.language.PATElem;
import com.hitorro.language.PennAndTreebankBase;
import com.hitorro.util.core.Console;

import java.util.List;

/**
 * Renderer for pretty-printing trees according to the Penn Treebank indenting
 * guidelines (mutliline).  Adapted from code originally written by Dan Klein
 * and modified by Chris Manning.
 */
public class PennTreeRenderer {

    /**
     * Print the tree as done in Penn Treebank merged files. The formatting
     * should be exactly the same, but we don't print the trailing whitespace
     * found in Penn Treebank trees. The basic deviation from a bracketed
     * indented tree is to in general collapse the printing of adjacent
     * preterminals onto one line of tags and words.  Additional complexities
     * are that conjunctions (tag CC) are not collapsed in this way, and that
     * the unlabeled outer brackets are collapsed onto the same line as the next
     * bracket down.
     */
    public static <L> String render(Tree<L> tree) {
        return renderHTML(tree, null);
    }

    public static <L> String renderHTML(Tree<L> tree, PennAndTreebankBase patb) {
        StringBuilder sb = new StringBuilder();

        renderHTMLTree(tree, 0, false, false, false, true, sb, patb);

        if (patb == null) {
            sb.append("\n");
        }


        return sb.toString();
    }

    /**
     * Display a node, implementing Penn Treebank style layout
     */
    private static <L> void renderHTMLTree(Tree<L> tree, int indent,
                                           boolean parentLabelNull, boolean firstSibling,
                                           boolean leftSiblingPreTerminal, boolean topLevel,
                                           StringBuilder sb, PennAndTreebankBase patb) {
        boolean suppressIndent = (parentLabelNull
                || (firstSibling && tree.isPreTerminal()) || (leftSiblingPreTerminal
                && tree.isPreTerminal() && (tree.getLabel() == null || !tree
                .getLabel().toString().startsWith("CC"))));
        if (suppressIndent) {
            if (patb == null) {
                sb.append(' ');
            } else {
                sb.append("&nbsp;");
            }
        } else {
            if (!topLevel) {
                if (patb == null) {
                    sb.append("\n");
                } else {
                    sb.append("<br/>");
                }
            }
            for (int i = 0; i < indent; i++) {
                if (patb == null) {
                    sb.append("  ");
                } else {
                    sb.append("&nbsp;");
                }
            }
        }
        if (tree.isLeaf() || tree.isPreTerminal()) {
            renderFlatHTML(tree, sb, patb);
            return;
        }
        sb.append('(');
        if (patb == null) {
            sb.append(tree.getLabel().toString());
        } else {
            renderWithBubble(sb, tree.getLabel().toString(), patb);
        }
        renderChildrenHTML(tree.getChildren(), indent + 1,
                tree.getLabel() == null
                        || tree.getLabel().toString() == null, sb, patb);
        sb.append(')');
    }

    private static <L> void renderFlatHTML(Tree<L> tree, StringBuilder sb, PennAndTreebankBase patb) {
        if (tree.isLeaf()) {
            sb.append(tree.getLabel().toString());
            return;
        }
        sb.append('(');
        if (patb == null) {
            sb.append(tree.getLabel().toString());
        } else {
            renderWithBubble(sb, tree.getLabel().toString(), patb);
        }
        sb.append(' ');
        sb.append(tree.getChildren().get(0).getLabel().toString());
        sb.append(')');
    }

    private static <L> void renderChildrenHTML(List<Tree<L>> children,
                                               int indent, boolean parentLabelNull, StringBuilder sb, PennAndTreebankBase patb) {
        boolean firstSibling = true;
        boolean leftSibIsPreTerm = true; // counts as true at beginning
        for (Tree<L> child : children) {
            renderHTMLTree(child, indent, parentLabelNull, firstSibling,
                    leftSibIsPreTerm, false, sb, patb);
            leftSibIsPreTerm = child.isPreTerminal();
            // CC is a special case
            if (child.getLabel() != null
                    && child.getLabel().toString().startsWith("CC")) {
                leftSibIsPreTerm = false;
            }
            firstSibling = false;
        }
    }

    private static void renderWithBubble(StringBuilder sb, String tok, PennAndTreebankBase patb) {
        if (patb == null) {
            sb.append(tok);
        }
        PATElem e = patb.getValue(tok.trim());
        if (e == null) {
            renderWithBubble(sb, tok, "<unknown>");
        } else {
            renderWithBubble(sb, tok, e.getDescription());
        }
    }

    private static void renderWithBubble(StringBuilder sb, String tok, String description) {
        Console.bprint(sb, "<div id=\"toc%s\" style=\"display: inline;\" title=\"%s\">%s</div> ", Trees.getTokId(), description, tok);
    }
}
