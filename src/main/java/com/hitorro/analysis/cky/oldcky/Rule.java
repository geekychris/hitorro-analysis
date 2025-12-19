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
package com.hitorro.analysis.cky.oldcky;

import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.core.string.StringUtil;

/**
 *
 */
public abstract class Rule {
    protected double prob;
    protected String key;
    protected String from;

    protected String v1;

    public Rule(String from, String v1, double prob) {
        this.prob = prob;
        this.key = v1;
        this.from = from;
        this.v1 = v1;
    }

    public static String key(String v1) {
        return v1;
    }

    public static String key(String v1, String v2) {
        return StringUtil.strcat(v1, "+", v2);
    }

    public String getFrom() {
        return from;
    }

    public boolean isUnary() {
        return true;
    }

    public String getKey() {
        return key;
    }

    public double getProb() {
        return prob;
    }

    public String getV1() {
        return v1;
    }

    public String toString() {
        return Fmt.S("key: %s, from:%s to:%s", key, from, v1);
    }

    public String toShort() {
        return Fmt.S("%s->%s", from, v1);
    }
}
