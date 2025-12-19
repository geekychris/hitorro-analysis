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

public abstract class ConfidenceEstimator {

    public static ConfidenceEstimator resolveByName(
            String confEstimatorName,
            DependencyParser depParser) {

        // Expected name format: Type*param1*param2*...
        String[] params = confEstimatorName.split("\\*");
        String type = params[0];

        if (type.equals("KDFix")) {
            // Expected format: KDFix*stddev*K
            double stddev = Double.parseDouble(params[1]);
            int K = Integer.parseInt(params[2]);
            return new ConfidenceEstimatorKDFix(stddev, K, depParser);
        }

        throw new RuntimeException("Unknown confidence estimator: "
                + confEstimatorName);
    }

    // Compute the confidence score for each predicted edge.
    public abstract double[] estimateConfidence(DependencyInstance inst);
}