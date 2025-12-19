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
package com.hitorro.analysis.mstparser;

import com.hitorro.analysis.mstparser.io.DependencyReader;

import java.io.IOException;

public class DependencyEvaluator {

    public static void evaluate(String act_file,
                                String pred_file,
                                String format, boolean hasConfidence) throws IOException {

        DependencyReader goldReader = DependencyReader.createDependencyReader(format);
        boolean labeled = goldReader.startReading(act_file);

        DependencyReader predictedReader;
        if (hasConfidence) {
            predictedReader =
                    DependencyReader.createDependencyReaderWithConfidenceScores(format);
        } else {
            predictedReader = DependencyReader.createDependencyReader(format);
        }
        boolean predLabeled = predictedReader.startReading(pred_file);

        if (labeled != predLabeled) {
            System.out.println("Gold file and predicted file appear to differ on whether or not they are labeled. Expect problems!!!");
        }


        int total = 0;
        int corr = 0;
        int corrL = 0;
        int numsent = 0;
        int corrsent = 0;
        int corrsentL = 0;
        int root_act = 0;
        int root_guess = 0;
        int root_corr = 0;

        DependencyInstance goldInstance = goldReader.getNext();
        DependencyInstance predInstance = predictedReader.getNext();

        while (goldInstance != null) {

            int instanceLength = goldInstance.length();

            if (instanceLength != predInstance.length()) {
                System.out.println("Lengths do not test on sentence " + numsent);
            }

            int[] goldHeads = goldInstance.heads;
            String[] goldLabels = goldInstance.deprels;
            int[] predHeads = predInstance.heads;
            String[] predLabels = predInstance.deprels;

            boolean whole = true;
            boolean wholeL = true;

            // NOTE: the first item is the root info added during nextInstance(), so we skip it.

            for (int i = 1; i < instanceLength; i++) {
                if (predHeads[i] == goldHeads[i]) {
                    corr++;
                    if (labeled) {
                        if (goldLabels[i].equals(predLabels[i])) {
                            corrL++;
                        } else {
                            wholeL = false;
                        }
                    }
                } else {
                    whole = false;
                    wholeL = false;
                }
            }
            total += instanceLength - 1; // Subtract one to not score fake root token

            if (whole) {
                corrsent++;
            }
            if (wholeL) {
                corrsentL++;
            }
            numsent++;

            goldInstance = goldReader.getNext();
            predInstance = predictedReader.getNext();
        }

        System.out.println("Tokens: " + total);
        System.out.println("Correct: " + corr);
        System.out.println("Unlabeled Accuracy: " + ((double) corr / total));
        System.out.println("Unlabeled Complete Correct: " + ((double) corrsent / numsent));
        if (labeled) {
            System.out.println("Labeled Accuracy: " + ((double) corrL / total));
            System.out.println("Labeled Complete Correct: " + ((double) corrsentL / numsent));
        }

    }

    public static void main(String[] args) throws IOException {
        String format = "CONLL";
        if (args.length > 2) {
            format = args[2];
        }

        evaluate(args[0], args[1], format, false);
    }

}
