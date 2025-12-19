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
package com.hitorro.analysis.lda;

import java.io.File;

public class Estimator {

    // output model
    protected Model trnModel;
    LDACmdOption option;

    public boolean init(LDACmdOption option) {
        this.option = option;
        trnModel = new Model();

        if (option.est) {
            if (!trnModel.initNewModel(option)) {
                return false;
            }
            trnModel.data.localDict.writeWordMap(option.dir + File.separator + option.wordMapFileName);
        } else if (option.estc) {
            if (!trnModel.initEstimatedModel(option)) {
                return false;
            }
        }

        return true;
    }

    public void estimate() {
        System.out.println("Sampling " + trnModel.niters + " iteration!");

        int lastIter = trnModel.liter;
        for (trnModel.liter = lastIter + 1; trnModel.liter < trnModel.niters + lastIter; trnModel.liter++) {
            System.out.println("Iteration " + trnModel.liter + " ...");

            // for all z_i
            for (int m = 0; m < trnModel.M; m++) {
                for (int n = 0; n < trnModel.data.docs[m].length; n++) {
                    // z_i = z[m][n]
                    // sample from p(z_i|z_-i, w)
                    int topic = sampling(m, n);
                    trnModel.z[m].set(n, topic);
                }// end for each word
            }// end for each document

            if (option.savestep > 0) {
                if (trnModel.liter % option.savestep == 0) {
                    System.out.println("Saving the model at iteration " + trnModel.liter + " ...");
                    computeTheta();
                    computePhi();
                    trnModel.saveModel("model-" + Conversion.ZeroPad(trnModel.liter, 5));
                }
            }
        }// end iterations

        System.out.println("Gibbs sampling completed!\n");
        System.out.println("Saving the final model!\n");
        computeTheta();
        computePhi();
        trnModel.liter--;
        trnModel.saveModel("model-final");
    }

    /**
     * Do sampling
     *
     * @param m document number
     * @param n word number
     * @return topic id
     */
    public int sampling(int m, int n) {
        // remove z_i from the count variable
        int topic = trnModel.z[m].get(n);
        int w = trnModel.data.docs[m].words[n];

        trnModel.nw[w][topic] -= 1;
        trnModel.nd[m][topic] -= 1;
        trnModel.nwsum[topic] -= 1;
        trnModel.ndsum[m] -= 1;

        double Vbeta = trnModel.V * trnModel.beta;
        double Kalpha = trnModel.K * trnModel.alpha;

        //do multinominal sampling via cumulative method
        for (int k = 0; k < trnModel.K; k++) {
            trnModel.p[k] = (trnModel.nw[w][k] + trnModel.beta) / (trnModel.nwsum[k] + Vbeta) *
                    (trnModel.nd[m][k] + trnModel.alpha) / (trnModel.ndsum[m] + Kalpha);
        }

        // cumulate multinomial parameters
        for (int k = 1; k < trnModel.K; k++) {
            trnModel.p[k] += trnModel.p[k - 1];
        }

        // scaled sample because of unnormalized p[]
        double u = Math.random() * trnModel.p[trnModel.K - 1];

        for (topic = 0; topic < trnModel.K; topic++) {
            if (trnModel.p[topic] > u) //sample topic w.r.t distribution p
            {
                break;
            }
        }

        // add newly estimated z_i to count variables
        trnModel.nw[w][topic] += 1;
        trnModel.nd[m][topic] += 1;
        trnModel.nwsum[topic] += 1;
        trnModel.ndsum[m] += 1;

        return topic;
    }

    public void computeTheta() {
        for (int m = 0; m < trnModel.M; m++) {
            for (int k = 0; k < trnModel.K; k++) {
                trnModel.theta[m][k] = (trnModel.nd[m][k] + trnModel.alpha) / (trnModel.ndsum[m] + trnModel.K * trnModel.alpha);
            }
        }
    }

    public void computePhi() {
        for (int k = 0; k < trnModel.K; k++) {
            for (int w = 0; w < trnModel.V; w++) {
                trnModel.phi[k][w] = (trnModel.nw[w][k] + trnModel.beta) / (trnModel.nwsum[k] + trnModel.V * trnModel.beta);
            }
        }
    }
}
