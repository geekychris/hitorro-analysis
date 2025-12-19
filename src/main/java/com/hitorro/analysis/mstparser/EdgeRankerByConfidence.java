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
import java.util.*;

public class EdgeRankerByConfidence {

    public void rankEdgesByConfidence(String act_file, String pred_file,
                                      String format) throws IOException {

        // Reader of file without confidence scores
        DependencyReader goldReader = DependencyReader
                .createDependencyReader(format);
        boolean labeled = goldReader.startReading(act_file);

        // Reader of file *with* confidence scores
        DependencyReader predictedReader = DependencyReader
                .createDependencyReaderWithConfidenceScores(format);
        boolean predLabeled = predictedReader.startReading(pred_file);

        if (labeled != predLabeled) {
            System.out
                    .println("Gold file and predicted file appear to differ on whether or not they are labeled. Expect problems!!!");
        }

        int instIndex = 0;

        DependencyInstance goldInstance = goldReader.getNext();
        DependencyInstance predInstance = predictedReader.getNext();

        LinkedList<PredictedEdge> allEdges = new LinkedList<PredictedEdge>();

        while (goldInstance != null) {

            int instanceLength = goldInstance.length();

            if (instanceLength != predInstance.length()) {
                System.out.println("Lengths do not test on sentence "
                        + instIndex);
            }

            int[] goldHeads = goldInstance.heads;
            int[] predHeads = predInstance.heads;
            double[] confScores = predInstance.confidenceScores;

            // NOTE: the first item is the root info added during
            // nextInstance(), so we skip it.
            for (int i = 1; i < instanceLength; i++) {
                boolean correct = (predHeads[i] == goldHeads[i]);
                PredictedEdge edge = new PredictedEdge(correct, confScores[i]);
                allEdges.add(edge);
            }

            instIndex++;
            goldInstance = goldReader.getNext();
            predInstance = predictedReader.getNext();
        }

        // Sort all the edges according to the confidence score
        Collections.sort(allEdges, new CompareByConfidenceScore());

        double averagePrecision = avgPrecOfIncorrectEdgesRanking(allEdges);
        System.out.println("Average-Precision: " + averagePrecision);
    }

    // Go over list of edges (sorted by edges confidence scores)
    // compute the precision every time an incorrect
    // edge is encountered.
    // Finally compute the average of all the precision values.
    // The maximal average-precision value is achieved if all the incorrect
    // edges are found a the beginning of the list and worst score if all
    // incorrect edges are at the end of the list.
    double avgPrecOfIncorrectEdgesRanking(List<PredictedEdge> edges) {
        int incorrectEdges = 0;
        int inspectedEdges = 0;
        double precSum = 0;

        Iterator<PredictedEdge> iter = edges.iterator();
        while (iter.hasNext()) {
            PredictedEdge edge = iter.next();
            inspectedEdges++;
            if (edge.correct == false) {
                // found incorrect edge
                incorrectEdges++;
                double prec = (double) incorrectEdges / inspectedEdges;
                precSum += prec;
            }
        }
        return precSum / incorrectEdges;
    }

    class PredictedEdge {
        boolean correct;
        double confScore;

        public PredictedEdge(boolean correct, double confScore) {
            this.correct = correct;
            this.confScore = confScore;
        }
    }

    class CompareByConfidenceScore implements Comparator<PredictedEdge> {
        public CompareByConfidenceScore() {
            super();
        }

        public int compare(PredictedEdge o1, PredictedEdge o2) {
            if (o1.confScore < o2.confScore) {
                return -1;
            }
            if (o2.confScore < o1.confScore) {
                return 1;
            }
            return 0;
        }
    }
}
