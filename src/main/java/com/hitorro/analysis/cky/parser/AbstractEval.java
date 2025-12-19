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

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
abstract class AbstractEval<L> {

    protected String str = "";

    private int exact = 0;
    private int total = 0;

    private int correctEvents = 0;
    private int guessedEvents = 0;
    private int goldEvents = 0;

    abstract Set<Object> makeObjects(Tree<L> tree);

    public void evaluate(Tree<L> guess, Tree<L> gold) {
        evaluate(guess, gold, new PrintWriter(System.out, true));
    }

    /* evaluates precision and recall by calling makeObjects() to make a
     * setPhrases of structures for guess Tree and gold Tree, and compares them
     * with each other.  */
    public double evaluate(Tree<L> guess, Tree<L> gold, PrintWriter pw) {
        Set<Object> guessedSet = makeObjects(guess);
        Set<Object> goldSet = makeObjects(gold);
        Set<Object> correctSet = new HashSet<Object>();
        correctSet.addAll(goldSet);
        correctSet.retainAll(guessedSet);

        correctEvents += correctSet.size();
        guessedEvents += guessedSet.size();
        goldEvents += goldSet.size();

        int currentExact = 0;
        if (correctSet.size() == guessedSet.size() &&
                correctSet.size() == goldSet.size()) {
            exact++;
            currentExact = 1;
        }
        total++;

        // guess.pennPrint(pw);
        // gold.pennPrint(pw);
        return displayPRF(str + " [Current] ",
                correctSet.size(), guessedSet.size(), goldSet.size(),
                currentExact, 1, pw);

    }

    private double displayPRF(String prefixStr,
                              int correct,
                              int guessed,
                              int gold,
                              int exact,
                              int total,
                              PrintWriter pw) {

        double precision = (guessed > 0 ? correct / (double) guessed : 1.0);
        double recall = (gold > 0 ? correct / (double) gold : 1.0);
        double f1 = ((precision > 0.0 && recall > 0.0) ?
                2.0 / (1.0 / precision + 1.0 / recall) :
                0.0);
        double exactMatch = exact / (double) total;

        pw.printf
                ("%s   P: %5.2f   R: %5.2f   F1: %5.2f   EX: %5.2f %n",
                        prefixStr,
                        100.0 * precision,
                        100.0 * recall,
                        100.0 * f1,
                        100.0 * exactMatch);

        return 100.0 * f1;
    }

    public double getAveragePrecision() {
        return (guessedEvents > 0 ? correctEvents / (double) guessedEvents : 1.0);
    }

    public double getAverageRecall() {
        return (goldEvents > 0 ? correctEvents / (double) goldEvents : 1.0);
    }

    public double getAverageF1() {
        return ((getAveragePrecision() > 0.0 && getAverageRecall() > 0.0) ?
                2.0 / (1.0 / getAveragePrecision() + 1.0 / getAverageRecall()) :
                0.0);
    }

    public double display(boolean verbose) {
        return display(verbose, new PrintWriter(System.out, true));
    }

    public double display(boolean verbose, PrintWriter pw) {
        return displayPRF(str + " [Average] ", correctEvents, guessedEvents, goldEvents, exact, total, pw);
    }
}
