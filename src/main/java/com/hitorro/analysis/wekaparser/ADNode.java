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
package com.hitorro.analysis.wekaparser;

import com.hitorro.util.core.CompOperEnum;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.string.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ADNode implements Node {
    int depth;
    String feature;
    double trueBoost;
    double falseBoost;
    CompOperEnum oper;
    double value;

    int index;
    List<ADNode> rightNodes = new ArrayList();
    List<ADNode> leftNodes = new ArrayList();
    private boolean right = false;

    public ADNode(int index, int depth, String feature, String operator, String value, String trueBoost) {
        this.index = index;
        this.depth = depth;
        this.feature = feature;
        this.oper = CompOperEnum.compContext.getByShortName(operator);
        this.value = Double.parseDouble(value);
        this.trueBoost = Double.parseDouble(trueBoost);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Console.bprint(sb, "(%s/%s)%s %s %s %s / %s", index, depth, feature, oper, value, trueBoost, falseBoost);
        for (ADNode node : leftNodes) {
            Console.bprintln(sb, "left(%s)", node.toString());
        }
        for (ADNode node : rightNodes) {
            Console.bprintln(sb, "right(%s)", node.toString());
        }
        return sb.toString();
    }

    public void switchToRight() {
        right = true;
    }

    public void addNode(ADNode node) {
        if (right) {
            Console.println("Adding under right %s  ===== %s", this, node);
            rightNodes.add(node);
        } else {
            Console.println("Adding under left %s  ===== %s", this, node);
            leftNodes.add(node);
        }
    }

    public String getCode() {
        StringBuilder sb = new StringBuilder();
        createCode(sb, this, 0);
        return sb.toString();
    }

    void createCode(StringBuilder sb, ADNode node, int depth) {
        String currPad = StringUtil.padToLength(' ', depth * 4);

        String currPadIn = StringUtil.padToLength(' ', (depth + 1) * 4);
        if (depth != 0) {
            Console.bprintln(sb, "%sif (%s %s %s) {", currPad, node.feature, node.oper.getName(), node.value);
            Console.bprintln(sb, "%stotal += %s", currPadIn, node.trueBoost);
        }

        for (ADNode n : node.leftNodes) {
            createCode(sb, n, depth + 1);
        }
        if (depth != 0) {
            Console.bprintln(sb, "%s} else {", currPad);
            Console.bprintln(sb, "%stotal += %s", currPadIn, node.falseBoost);
        }
        for (ADNode n : node.rightNodes) {
            createCode(sb, n, depth + 1);
        }
        if (depth != 0) {
            Console.bprintln(sb, "%s}", currPad);
        }
    }
}
