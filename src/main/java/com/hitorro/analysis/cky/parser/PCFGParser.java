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
import com.hitorro.analysis.cky.model.Grammar;
import com.hitorro.analysis.cky.model.Lexicon;
import com.hitorro.analysis.cky.model.TreeAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The PCFG CFGParserInterface you will implement.
 */
public class PCFGParser implements CFGParserInterface {

    CKYParser p;
    private Grammar grammar;
    private Lexicon lexicon;
    private long total = 0;
    private long count = 0;

    public void train(List<Tree<String>> trainTrees) {
        // TODO: before you generate your grammar, the training trees
        // need to be binarized so that rules are at most binary


        List<Tree<String>> binary = new ArrayList<Tree<String>>();

        for (Tree<String> t : trainTrees) {
            Tree<String> t2 = TreeAnnotations.verticalMarkovization(t);

            //System.out.println(Trees.PennTreeRenderer.render(t));
            // System.out.println(Trees.PennTreeRenderer.render(t2));
            //System.out.println(Trees.PennTreeRenderer.render(de));

            binary.add(TreeAnnotations.annotateTree(t2));

        }
        lexicon = new Lexicon(binary);
        grammar = new Grammar(binary);
        p = new CKYParser(grammar, lexicon);
    }

    public Tree<String> getBestParse(List<String> sentence) {
        count++;
        long start = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        for (String s : sentence) {
            sb.append(s);
            sb.append(" ");
        }

        p.parse(sentence.toArray(new String[sentence.size()]));
        //p.printCounts();
        Tree<String> tree = p.getBacktrace("ROOT");
        if (tree == null) {
            tree = new Tree<String>("empty");
        }
        tree = TreeAnnotations.unAnnotateTree(tree);
        Tree<String> de = TreeAnnotations.deMarkovization(tree);
        long end = System.currentTimeMillis() - start;
        total += end;
        System.out.println("Sentence: " + sb.toString() + " took: " + end + " avg: " + total / count + " totalTime: " + total / 1000);
        return de;
    }


    private Tree<String> addRoot(Tree<String> tree) {
        return new Tree<String>("ROOT", Collections.singletonList(tree));
    }

    private Tree<String> buildTagTree(List<String> words,
                                      List<String> tags,
                                      int currentPosition) {
        Tree<String> leafTree = new Tree<String>(words.get(currentPosition));
        Tree<String> tagTree = new Tree<String>(tags.get(currentPosition),
                Collections.singletonList(leafTree));
        return tagTree;
    }

    private List<String> getBaselineTagging(List<String> sentence) {
        List<String> tags = new ArrayList<String>();
        for (String word : sentence) {
            String tag = getBestTag(word);
            tags.add(tag);
        }
        return tags;
    }

    private String getBestTag(String word) {
        double bestScore = Double.NEGATIVE_INFINITY;
        String bestTag = null;
        for (String tag : lexicon.getAllTags()) {
            double score = lexicon.scoreTagging(word, tag);
            if (bestTag == null || score > bestScore) {
                bestScore = score;
                bestTag = tag;
            }
        }
        return bestTag;
    }

}
