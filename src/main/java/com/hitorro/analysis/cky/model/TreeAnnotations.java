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
package com.hitorro.analysis.cky.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.analysis.cky.ling.Trees;
import com.hitorro.analysis.cky.ling.transformers.FunctionNodeStripper;
import com.hitorro.util.core.opers.HTPredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Class which contains code for annotating and binarizing trees for
 * the parser's use, and debinarizing and unannotating them for
 * scoring.
 */
public class TreeAnnotations {         //73.09
    public static final String PhraseTags[] = new String[]{
            //  "ADJP",                        //72.09   neg
            //    "ADVP",                       //72.94 neg
            //    "CONJP",                      //73.09 neut
            //    "FRAG",                       // 72.51 -neg
            //"INTJ",                           //72.98 -neg
            //    "LST",     //73.11  + 0.02
            //      "NAC",                      //73.09 neut
            //    "NP",      // 73.16  + 0.07
            //"NX",                             //73.09  neut
            "PP",        //73.46 + 0.37
            //    "PRN",                          //73.09
            //    "PRT",                            //73.09
            //"QP",                               //73.09
            //    "RRC",                          //73.09
            //    "UCP",                          //73.09
            "VP",         //73.69 +0.60
            //"WHADJP",                             //73.09
            //"WHAVP",                              //73.09
            // "WHNP",                                //73.11
            // "WHPP"           //73.15 + 0.06

    };

    private static HashSet<String> secondOrderMarkovizerSet = getMarkovPOSSet();

    /**
     * List of name POS tags that require the last of the listChildren to be ordained with their parents tag
     * http://bulba.sdsu.edu/jeanette/thesis/PennTags.html
     * Only do phrase level tags:
     * Phrase Level
     * ADJP - Adjective Phrase.
     * ADVP - Adverb Phrase.
     * CONJP - Conjunction Phrase.
     * FRAG - Fragment.
     * <p>
     * INTJ - Interjection. Corresponds approximately to the part-of-speech tag UH.
     * LST - List marker. Includes surrounding punctuation.
     * NAC - Not a Constituent; used to show the scope of certain prenominal modifiers within an NP.
     * NP - Noun Phrase.
     * <p>
     * NX - Used within certain complex NPs to mark the head of the NP. Corresponds very roughly to N-bar level but used quite differently.
     * PP - Prepositional Phrase.
     * PRN - Parenthetical.
     * PRT - Particle. Category for words that should be tagged RP.
     * <p>
     * QP - Quantifier Phrase (i.e. complex measure/amount phrase); used within NP.
     * RRC - Reduced Relative Clause.
     * UCP - Unlike Coordinated Phrase.
     * VP - Vereb Phrase.
     * <p>
     * WHADJP - Wh-adjective Phrase. Adjectival phrase containing a wh-adverb, as in how hot.
     * WHAVP - Wh-adverb Phrase. Introduces a clause with an NP gap. May be null (containing the 0 complementizer) or lexical, containing a wh-adverb such as how or why.
     * WHNP - Wh-noun Phrase. Introduces a clause with an NP gap. May be null (containing the 0 complementizer) or lexical, containing some wh-word, e.g. who, which book, whose daughter, none of which, or how many leopards.
     * WHPP - Wh-prepositional Phrase. Prepositional phrase containing a wh-noun phrase (such as of which or by whose authority) that either introduces a PP gap or is contained by a WHNP.
     * <p>
     * X - Unknown, uncertain, or unbracketable. X is often used for bracketing typos and in bracketing the...the-constructions.
     *
     * @return
     */
    private static HashSet<String> getMarkovPOSSet() {
        HashSet<String> set = new HashSet<String>();
        for (String tok : PhraseTags) {
            set.add(tok);
        }
        return set;
    }

    public static Tree<String> verticalMarkovization(Tree<String> unAnnotatedTree) {
        return secondOrderMarkovize(unAnnotatedTree);
    }

    public static Tree<String> deMarkovization(Tree<String> unAnnotatedTree) {
        return deMarkovize(unAnnotatedTree);
    }


    private static Tree<String> secondOrderMarkovize(Tree<String> tree) {
        String label = tree.getLabel();

        if (tree.isLeaf()) {
            return new Tree<String>(label);
        }

        Tree<String> intermediateTree =
                markovizeTreeHelper(tree, label);
        return new Tree<String>(label, intermediateTree.getChildren());
    }

    private static Tree<String> deMarkovize(Tree<String> tree) {
        String label = deMarkovizeLabel(tree.getLabel());

        if (tree.isLeaf()) {
            return new Tree<String>(label);
        }

        Tree<String> intermediateTree =
                deMarkovizeTreeHelper(tree, label);
        return new Tree<String>(label, intermediateTree.getChildren());
    }

    private static String deMarkovizeLabel(String label) {
        int indexOf = label.indexOf("^");
        if (indexOf == -1) {
            return label;
        }
        return label.substring(0, indexOf);
    }

    /**
     * copy the tree and add name name to child if the name is in the
     *
     * @param tree
     * @param parentLabel
     * @return
     */
    private static Tree<String> markovizeTreeHelper(Tree<String> tree,
                                                    String parentLabel) {

        List<Tree<String>> children = new ArrayList<Tree<String>>();
        List<Tree<String>> oldChildren = tree.getChildren();
        String label = tree.getLabel();
        for (int i = 0; i < oldChildren.size(); i++) {
            children.add(markovizeTreeHelper(oldChildren.get(i), label));
        }

        if (secondOrderMarkovizerSet.contains(label)) {
            // our tag is a phrase tag and therefor we will adorn with name tag
            label = label + "^" + parentLabel;
        }
        return new Tree<String>(label, children);
    }


    private static Tree<String> deMarkovizeTreeHelper(Tree<String> tree,
                                                      String intermediateLabel) {

        List<Tree<String>> children = new ArrayList<Tree<String>>();
        List<Tree<String>> oldChildren = tree.getChildren();
        String label = deMarkovizeLabel(tree.getLabel());
        for (int i = 0; i < oldChildren.size(); i++) {
            children.add(deMarkovizeTreeHelper(oldChildren.get(i), intermediateLabel));
        }

        return new Tree<String>(label, children);
    }

    public static Tree<String> annotateTree(Tree<String> unAnnotatedTree) {

        // Currently, the only annotation done is a lossless binarization

        // TODO: change the annotation from a lossless binarization to a
        // finite-order markov process (try at least 1st and 2nd order)
        // mark nodes with the label of their name nodes, giving a second
        // order vertical markov process

        return binarizeTree(unAnnotatedTree);

    }


    private static Tree<String> binarizeTree(Tree<String> tree) {
        String label = tree.getLabel();
        if (tree.isLeaf()) {
            return new Tree<String>(label);
        }
        if (tree.getChildren().size() == 1) {
            return new Tree<String>
                    (label,
                            Collections.singletonList(binarizeTree(tree.getChildren().get(0))));
        }
        // otherwise, it's a binary-or-more local tree,
        // so decompose it into a sequence of binary and unary trees.
        String intermediateLabel = "@" + label + "->";
        Tree<String> intermediateTree =
                binarizeTreeHelper(tree, 0, intermediateLabel);
        return new Tree<String>(label, intermediateTree.getChildren());
    }

    private static Tree<String> binarizeTreeHelper(Tree<String> tree,
                                                   int numChildrenGenerated,
                                                   String intermediateLabel) {
        Tree<String> leftTree = tree.getChildren().get(numChildrenGenerated);
        List<Tree<String>> children = new ArrayList<Tree<String>>();
        children.add(binarizeTree(leftTree));
        if (numChildrenGenerated < tree.getChildren().size() - 1) {
            Tree<String> rightTree =
                    binarizeTreeHelper(tree, numChildrenGenerated + 1,
                            intermediateLabel + "_" + leftTree.getLabel());
            children.add(rightTree);
        }
        return new Tree<String>(intermediateLabel, children);
    }

    public static Tree<String> unAnnotateTree(Tree<String> annotatedTree) {

        // Remove intermediate nodes (labels beginning with "@"
        // Remove all material on node labels which follow their base symbol
        // (cuts at the leftmost -, ^, or : character)
        // Examples: a node with label @NP->DT_JJ will be spliced out,
        // and a node with label NP^S will be reduced to NP

        Tree<String> debinarizedTree =
                Trees.spliceNodes(annotatedTree, new HTPredicate<String>() {
                    @Override
                    public void initForPass() {

                    }

                    public boolean test(String s) {
                        return s.startsWith("@");
                    }

                    @Override
                    public boolean initFromMap(final JsonNode map) {
                        return true;
                    }
                });
        Tree<String> unAnnotatedTree =
                (new FunctionNodeStripper()).apply(debinarizedTree);
        return unAnnotatedTree;
    }
}
