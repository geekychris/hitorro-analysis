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

import com.hitorro.util.core.Console;
import com.hitorro.util.core.string.Fmt;

import java.text.DecimalFormat;


public class CellRow {
    Rule rule;
    double prob;
    CellRow left;
    CellRow right;

    public CellRow(Rule r, double prob, CellRow left, CellRow right) {
        this.rule = r;
        this.prob = prob;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return Fmt.S("CellRow: %s prob %s", rule, prob);
    }

    public String getTree() {

        DecimalFormat df = new DecimalFormat("#.######");
        StringBuilder sb = new StringBuilder();
        Console.bprint(sb, "%s p=%s ", rule.from, df.format(prob));
        if (left != null || right != null) {
            sb.append("(");
            if (left != null) {
                sb.append(left.getTree());

            }
            if (right != null) {
                sb.append(right.getTree());

            }
            sb.append(")\n");
        } else {
            // leaf unary
            sb.append(rule.v1);
        }
        return sb.toString();
    }

    public String getShort() {
        DecimalFormat df = new DecimalFormat("#.######");
        return Fmt.S("%s %s", rule.toShort(), df.format(prob));
    }
}

