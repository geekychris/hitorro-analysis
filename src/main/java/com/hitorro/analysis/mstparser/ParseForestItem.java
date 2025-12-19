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

public class ParseForestItem {

    public int s, r, t, dir, comp, length, type;
    public double prob;
    public FeatureVector fv;
    public ParseForestItem left, right;

    // productions
    public ParseForestItem(int i, int k, int j, int type,
                           int dir, int comp,
                           double prob, FeatureVector fv,
                           ParseForestItem left, ParseForestItem right) {
        this.s = i;
        this.r = k;
        this.t = j;
        this.dir = dir;
        this.comp = comp;
        this.type = type;
        length = 6;

        this.prob = prob;
        this.fv = fv;

        this.left = left;
        this.right = right;

    }

    // preproductions
    public ParseForestItem(int s, int type, int dir, double prob, FeatureVector fv) {
        this.s = s;
        this.dir = dir;
        this.type = type;
        length = 2;

        this.prob = prob;
        this.fv = fv;

        left = null;
        right = null;

    }

    public ParseForestItem() {
    }

    public void copyValues(ParseForestItem p) {
        p.s = s;
        p.r = r;
        p.t = t;
        p.dir = dir;
        p.comp = comp;
        p.prob = prob;
        p.fv = fv;
        p.length = length;
        p.left = left;
        p.right = right;
        p.type = type;
    }

    // way forest works, only have to check rule and indeces
    // for equality.
    public boolean equals(ParseForestItem p) {
        return s == p.s && t == p.t && r == p.r
                && dir == p.dir && comp == p.comp
                && type == p.type;
    }

    public boolean isPre() {
        return length == 2;
    }

}
