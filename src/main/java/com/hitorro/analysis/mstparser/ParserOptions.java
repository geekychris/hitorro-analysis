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

import java.io.File;

/**
 * Hold all the options for the parser so they can be passed around easily. <p/> <p> Created: Sat Nov 10 15:25:10 2001
 * </p>
 *
 * @author Jason Baldridge
 * @version $Id: CONLLReader.java 103 2007-01-21 20:26:39Z jasonbaldridge $
 * @see mstparser.io.DependencyReader
 */
public final class ParserOptions {

    public String trainfile = null;
    public String testfile = null;
    public File trainforest = null;
    public File testforest = null;
    public boolean train = false;
    public boolean eval = false;
    public boolean test = false;
    public boolean rankEdgesByConfidence = false;
    public String modelName = "dep.model";
    public String lossType = "punc";
    public boolean createForest = true;
    public String decodeType = "proj";
    public String format = "CONLL";
    public int numIters = 10;
    public String outfile = "out.txt";
    public String goldfile = null;
    public int trainK = 1;
    public int testK = 1;
    public boolean secondOrder = false;
    public boolean useRelationalFeatures = false;
    public boolean discourseMode = false;
    public String confidenceEstimator = null;

    public ParserOptions(String[] args) {

        for (int i = 0; i < args.length; i++) {
            String[] pair = args[i].split(":");

            if (pair[0].equals("train")) {
                train = true;
            }
            if (pair[0].equals("eval")) {
                eval = true;
            }
            if (pair[0].equals("test")) {
                test = true;
            }
            if (pair[0].equals("iters")) {
                numIters = Integer.parseInt(pair[1]);
            }
            if (pair[0].equals("output-file")) {
                outfile = pair[1];
            }
            if (pair[0].equals("gold-file")) {
                goldfile = pair[1];
            }
            if (pair[0].equals("train-file")) {
                trainfile = pair[1];
            }
            if (pair[0].equals("test-file")) {
                testfile = pair[1];
            }
            if (pair[0].equals("model-name")) {
                modelName = pair[1];
            }
            if (pair[0].equals("training-k")) {
                trainK = Integer.parseInt(pair[1]);
            }
            if (pair[0].equals("loss-type")) {
                lossType = pair[1];
            }
            if (pair[0].equals("order") && pair[1].equals("2")) {
                secondOrder = true;
            }
            if (pair[0].equals("create-forest")) {
                createForest = pair[1].equals("true") ? true : false;
            }
            if (pair[0].equals("decodeAux-type")) {
                decodeType = pair[1];
            }
            if (pair[0].equals("format")) {
                format = pair[1];
            }
            if (pair[0].equals("relational-features")) {
                useRelationalFeatures = pair[1].equals("true") ? true : false;
            }
            if (pair[0].equals("discourse-mode")) {
                discourseMode = pair[1].equals("true") ? true : false;
            }
            if (pair[0].equals("confidence-estimation")) {
                confidenceEstimator = pair[1];
            }
            if (pair[0].equals("rankEdgesByConfidence")) {
                rankEdgesByConfidence = true;
            }
        }


        try {
            File tmpDir = new File("/tmp");
            if (null != trainfile) {
                trainforest = File.createTempFile("train", ".forest");
                trainforest.deleteOnExit();
            }

            if (null != testfile) {
                testforest = File.createTempFile("test", ".forest");
                testforest.deleteOnExit();
            }

        } catch (java.io.IOException e) {
            System.out.println("Unable to create tmp files for feature forests!");
            System.out.println(e);
            System.exit(0);
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FLAGS [");
        sb.append("train-file: " + trainfile);
        sb.append(" | ");
        sb.append("test-file: " + testfile);
        sb.append(" | ");
        sb.append("gold-file: " + goldfile);
        sb.append(" | ");
        sb.append("output-file: " + outfile);
        sb.append(" | ");
        sb.append("model-name: " + modelName);
        sb.append(" | ");
        sb.append("train: " + train);
        sb.append(" | ");
        sb.append("test: " + test);
        sb.append(" | ");
        sb.append("eval: " + eval);
        sb.append(" | ");
        sb.append("loss-type: " + lossType);
        sb.append(" | ");
        sb.append("second-order: " + secondOrder);
        sb.append(" | ");
        sb.append("training-iterations: " + numIters);
        sb.append(" | ");
        sb.append("training-k: " + trainK);
        sb.append(" | ");
        sb.append("decodeAux-type: " + decodeType);
        sb.append(" | ");
        sb.append("create-forest: " + createForest);
        sb.append(" | ");
        sb.append("format: " + format);
        sb.append(" | ");
        sb.append("relational-features: " + useRelationalFeatures);
        sb.append(" | ");
        sb.append("discourse-mode: " + discourseMode);
        sb.append("]\n");
        return sb.toString();
    }
}
