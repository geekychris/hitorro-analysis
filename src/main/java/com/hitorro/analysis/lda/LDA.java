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


public class LDA {

    public void execute() {
        LDACmdOption option = new LDACmdOption();


        if (option.est || option.estc) {
            Estimator estimator = new Estimator();
            estimator.init(option);
            estimator.estimate();
        } else if (option.inf) {
            Inferencer inferencer = new Inferencer();
            inferencer.init(option);

            Model newModel = inferencer.inference();

            for (int i = 0; i < newModel.phi.length; ++i) {
                //phi: K * V
                System.out.println("-----------------------\ntopic" + i + " : ");
                for (int j = 0; j < 10; ++j) {
                    System.out.println(inferencer.globalDict.id2word.get(j) + "\t" + newModel.phi[i][j]);
                }
            }
        }


    }
}
