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
package com.hitorro.analysis.cky.model;

import java.util.HashSet;


public abstract class Rule {
    String name;
    int parentId;

    double score;

    private HashSet<String> rights = new HashSet<String>();
    private String rightsS[] = null;

    public final String getName() {
        return name;
    }


    public int getParentId() {
        return parentId;
    }

    private void finalizeStructs() {
        rightsS = new String[rights.size()];
        int i = 0;
        for (String s : rights) {
            rightsS[i++] = s;
        }
    }

    public String[] getRightVals() {
        if (rightsS == null) {
            finalizeStructs();
        }
        return rightsS;
    }


    public final double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


    public boolean isUnary() {
        return true;
    }

    public abstract String toShort();

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Rule) {
            return name.equals(((Rule) o).name);
        }
        return false;
    }
}
