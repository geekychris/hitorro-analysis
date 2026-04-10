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
package com.hitorro.analysis.cky.parser;

import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.analysis.cky.model.Rule;
import com.hitorro.analysis.cky.model.UnaryRule;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class CellRow {
    private Rule rule;
    private double prob;
    private CellRow left;
    private CellRow right;
    private int index;

    private CellRow(int index) {
        this.index = index;
    }

    public final static CellRow get(int index) {
        return new CellRow(index);
    }

    public final void set(final Rule r, final double prob, final CellRow left, final CellRow right) {
        this.rule = r;
        this.prob = prob;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "CellRow: " + rule + " prob %s" + prob;
    }

    /**
     * @return
     */
    public final Tree<String> getTree() {
        /*DecimalFormat df = new DecimalFormat("#.######");
        StringBuilder sb = new StringBuilder();
        Console.bprint(sb, "%s p=%s ", rule.from, df.format(prob));
        if (left != null || right != null)
        {
            sb.append("(");
            if (left != null)
            {
                sb.append(left.getTree());

            }
            if (right != null)
            {
                sb.append(right.getTree());

            }
            sb.append(")\n");
        }
        else
        {
            // leaf unary
            sb.append(rule.v1);
        }
        if (left != null || right != null)
        {
            sb.append("(");
            if (left != null)
            {
                sb.append(left.getTree());

            }
            if (right != null)
            {
                sb.append(right.getTree());

            }
            sb.append(")\n");
        }
        else
        {
            // leaf unary
            sb.append(rule.v1);
        }
        return sb.toString();   */

        if (left != null || right != null) {
            ArrayList<Tree<String>> list = new ArrayList<Tree<String>>();

            if (left != null) {
                list.add(left.getTree());

            }
            if (right != null) {
                list.add(right.getTree());
            }
            return new Tree<String>(rule.getName(), list);
        } else {
            ArrayList<Tree<String>> al = new ArrayList<Tree<String>>();
            al.add(new Tree<String>(((UnaryRule) rule).getChild()));
            return new Tree<String>(rule.getName(), al);
        }
    }

    public final String getShort() {
        DecimalFormat df = new DecimalFormat("#.######");
        return rule.toShort() + " " + df.format(prob);
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(final Rule rule) {
        this.rule = rule;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(final double prob) {
        this.prob = prob;
    }

    public CellRow getLeft() {
        return left;
    }

    public void setLeft(final CellRow left) {
        this.left = left;
    }

    public CellRow getRight() {
        return right;
    }

    public void setRight(final CellRow right) {
        this.right = right;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }
}


