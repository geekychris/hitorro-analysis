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

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 4/28/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */ /* A binary grammar rule with score representing its probability. */
public class BinaryRule extends Rule {

    String leftChild;
    String rightChild;


    public BinaryRule(String parent, String leftChild, String rightChild, Grammar g) {
        this.name = parent;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.parentId = g.idMap.getOrAdd(this, parent);
    }

    public String toShort() {
        return name + "->" + leftChild + " " + rightChild;
    }

    public final String getLeftChild() {
        return leftChild;
    }

    public final String getRightChild() {
        return rightChild;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BinaryRule)) {
            return false;
        }

        final BinaryRule binaryRule = (BinaryRule) o;

        if (leftChild != null ? !leftChild.equals(binaryRule.leftChild) : binaryRule.leftChild != null) {
            return false;
        }
        if (name != null ? !name.equals(binaryRule.name) : binaryRule.name != null) {
            return false;
        }
        if (rightChild != null ? !rightChild.equals(binaryRule.rightChild) : binaryRule.rightChild != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 29 * result + (leftChild != null ? leftChild.hashCode() : 0);
        result = 29 * result + (rightChild != null ? rightChild.hashCode() : 0);
        return result;
    }

    public String toString() {
        return name + " -> " + leftChild + " " + rightChild + " %% " + score;
    }

    public boolean isUnary() {
        return true;
    }
}
