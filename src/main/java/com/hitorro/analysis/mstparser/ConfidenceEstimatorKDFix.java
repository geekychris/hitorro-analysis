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

import java.util.Random;

public class ConfidenceEstimatorKDFix extends ConfidenceEstimator {
    // The number of parses to use for estimation.
    int k;
    // The Std-Dev to use for drawing the K parameter vectors.
    double stddev;
    Parameters parameters[];
    DependencyParser depParser;

    public ConfidenceEstimatorKDFix(double stddev, int k,
                                    DependencyParser depParser) {
        this.stddev = stddev;
        this.k = k;
        this.depParser = depParser;
        drawKParameterVectors();
    }

    // If the model parameters of the dependency parser are updated then
    // this method must be called to re-draw the K parameter vectors.
    public void drawKParameterVectors() {
        Random rand = new Random();
        // This is the base model parameters.
        double[] modelWeights = depParser.getParams().parameters;
        int numParams = modelWeights.length;

        // Draw K parameter vectors
        parameters = new Parameters[k];
        for (int i = 0; i < k; i++) {
            double[] params = new double[numParams];

            // Draw each of the parameters according to the base model
            // parameter and the required Std-Dev.
            for (int j = 0; j < numParams; j++) {
                double gauss = rand.nextGaussian();
                double mean = modelWeights[j];
                params[j] = mean + gauss * stddev;
            }
            parameters[i] = new Parameters(params);
        }
    }

    // /////////////////////////////////////////////////////////////////
    // Compute the confidence score for each predicted edge.
    // /////////////////////////////////////////////////////////////////
    @Override
    public double[] estimateConfidence(DependencyInstance inst) {
        // Parse the instance using the base parser parameters.
        int[] predictedHeads = new int[inst.heads.length - 1];
        depParser.decode(inst, 1, depParser.getParams(),
                predictedHeads);

        int[][] alternativeHeads = new int[k][predictedHeads.length];
        // Generate K alternative parses using the K sampled parameters vectors.
        // Note: Using multi-threaded implementation.
        produceKAlternatives_MT(inst, alternativeHeads);

        return confidenceScoresByAgreement(predictedHeads, alternativeHeads);
    }

    double[] confidenceScoresByAgreement(int[] prediction, int[][] alternatives) {
        // For each prediction count the number of alternatives
        // that agree with the prediction.
        double[] confidenceScores = new double[prediction.length];
        for (int h = 0; h < prediction.length; h++) {
            int agreeCount = 0;
            for (int i = 0; i < k; i++) {
                if (prediction[h] == alternatives[i][h]) {
                    agreeCount++;
                }
            }

            // The confidence score is the fraction of alternatives that
            // agree with the prediction.
            confidenceScores[h] = (double) agreeCount / k;
        }
        return confidenceScores;
    }

    // /////////////////////////////////////////////////////////////////
    // Multi-threaded implementation of produce-K-Alternatives.
    // /////////////////////////////////////////////////////////////////
    void produceKAlternatives_MT(DependencyInstance inst,
                                 int[][] alternativeHeads) {
        DecoderRunner[] runners = new DecoderRunner[k];
        // Generate K alternative parses using the K sampled parameters vectors.
        for (int i = 0; i < k; i++) {
            runners[i] = new DecoderRunner(inst, parameters[i],
                    alternativeHeads[i]);
            runners[i].start();
        }
        // Wait for all decoder threads to complete
        for (int i = 0; i < k; i++) {
            try {
                runners[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // /////////////////////////////////////////////////////////////////
    // Single threaded implementation of produce-K-Alternatives.
    // /////////////////////////////////////////////////////////////////
    void produceKAlternatives_ST(DependencyInstance inst,
                                 int[][] alternativeHeads) {
        // Generate K alternative parses using the K sampled parameters vectors.
        for (int i = 0; i < k; i++) {
            depParser.decode(inst, 1, parameters[i],
                    alternativeHeads[i]);
        }
    }

    class DecoderRunner extends Thread {
        DependencyInstance inst;
        Parameters parameters;
        int[] heads;

        public DecoderRunner(DependencyInstance inst, Parameters parameters,
                             int[] heads) {
            super();
            this.inst = inst;
            this.parameters = parameters;
            this.heads = heads;
        }

        public void run() {
            depParser.decode(inst, 1, parameters, heads);
        }
    }
}
