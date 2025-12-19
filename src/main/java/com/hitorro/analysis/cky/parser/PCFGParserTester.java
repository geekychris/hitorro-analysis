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

import com.hitorro.analysis.cky.io.NumberRangeFileFilter;
import com.hitorro.analysis.cky.io.PennFileMapper;
import com.hitorro.analysis.cky.ling.PennTreeRenderer;
import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.analysis.cky.ling.transformers.StandardTreeNormalizer;
import com.hitorro.obj.core.Log;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.ArrayIterator;

import java.io.File;
import java.util.*;


/**
 * Harness for PCFG CFGParserInterface project.
 *
 * @author Dan Klein
 */
public class PCFGParserTester {

    // Longest sentence length that will be tested on.

    private static LabeledConstituentEval<String> testParser(CFGParserInterface parser, List<Tree<String>> testTrees, int maxLength) {
        LabeledConstituentEval<String> eval =
                new LabeledConstituentEval<String>
                        (Collections.singleton("ROOT"),
                                new HashSet<String>(Arrays.asList("''", "``", ".", ":", ",")));
        for (Tree<String> testTree : testTrees) {
            List<String> testSentence = testTree.getYield();

            if (testSentence.size() > maxLength) {
                continue;
            }
            Tree<String> guessedTree = parser.getBestParse(testSentence);
            System.out.println("Guess:\n" + PennTreeRenderer.render(guessedTree));
            System.out.println("Gold:\n" + PennTreeRenderer.render(testTree));
            eval.evaluate(guessedTree, testTree);
        }
        eval.display(true);
        return eval;
    }

    public static AbstractIterator<Tree<String>> readMASCTrees(File basePath, int low, int high) {
        NumberRangeFileFilter fileFilter = new NumberRangeFileFilter(".mrg", low, high);
        ArrayIterator<File> files = new ArrayIterator<File>(basePath.listFiles(fileFilter));
        AbstractIterator<Tree<String>> trees = files.nest(PennFileMapper.instance);

        return trees.map(StandardTreeNormalizer.instance);
    }

    public static LabeledConstituentEval<String> execute(final CFGParserInterface parser, File path, int maxSentenceLength) {
        List<Tree<String>> trainTrees;

        List<Tree<String>> testTrees;


        // training data: MASC train
        Log.cky.info("Loading MASC training trees... from: %smasc/train", path);
        trainTrees = readMASCTrees(new File(path, "masc/train"), 0, 38).toCollection(new ArrayList(), -1);
        Log.cky.info("done.");
        Log.cky.info("Train trees size: " + trainTrees.size());

        Log.cky.info("First train tree: %s", PennTreeRenderer.render(trainTrees.get(0)));
        Log.cky.info("Last train tree: %s", PennTreeRenderer.render(trainTrees.get(trainTrees.size() - 1)));

        // test data: MASC devtest
        Log.cky.info("Loading MASC test trees...");
        testTrees = readMASCTrees(new File(path, "masc/devtest"), 0, 11).toCollection(new ArrayList(), -1);
        //testTrees.addAll(readMASCTrees(basePath+"masc/blindtest", 0, 8));
        Log.cky.info("Test trees size: %s", testTrees.size());
        Log.cky.info("done.");

        Log.cky.info("First test tree: %s", PennTreeRenderer.render(testTrees.get(0)));
        Log.cky.info("Last test tree: %s", PennTreeRenderer.render(testTrees.get(testTrees.size() - 1)));


        Log.cky.info("Training parser...");
        parser.train(trainTrees);

        Log.cky.info("Testing parser...");
        return testParser(parser, testTrees, maxSentenceLength);
    }
}
