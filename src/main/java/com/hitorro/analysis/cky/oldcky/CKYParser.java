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
import com.hitorro.util.core.string.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CKYParser implements RuleReceiver {
    protected Map<String, ArrayList<Rule>> rules = new HashMap();

    private CellResult results[][] = null;
    private int longestKey = 0;

    public boolean parse(String tokens[]) {
        int n = tokens.length;
        results = fill(results, tokens.length + 1);
        // fill 0,0->0,l-1 with POS from the input tokens (this would ultimately be a toks to POS tags
        boolean allThere = true;
        for (int i = 0; i < tokens.length; i++) {
            int j = i + 1;
            ArrayList<Rule> r = rules.get(tokens[i]);

            CellResult cr = results[i][j];
            cr.clear();
            if (r != null) {
                // starter prob is 1 so that pr * 1 = pr
                cr.add(r, 1.0);
                cr.resolveUnaries(this);
            } else {
                allThere = false;
            }
        }
        if (!allThere) {
            return false;
        }

        for (int span = 2; span <= n; span++) {
            int wordsMinusSpan = n - span;
            for (int begin = 0; begin <= wordsMinusSpan; begin++) {
                int end = begin + span;
                CellResult cr = results[begin][end];
                cr.clear();
                int found = 0;
                for (int split = begin + 1; split <= end - 1; split++) {
                    //Console.println("[%s,%s] from [%s,%s] and [%s,%s]", begin,end, begin, split, split, end);
                    CellResult lm = results[begin][split];
                    CellResult mr = results[split][end];
                    found += cr.addIntersect(lm, mr, this);
                }
                if (found > 0) {
                    cr.resolveUnaries(this);
                }
            }
        }

        return true;
    }

    public String getTree(String node) {
        CellResult cr = results[0][results.length - 1];
        CellRow row = cr.map.get(node);
        if (row == null) {
            return "";
        }
        return row.getTree();
    }

    public void print() {
        int pack = 20;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < results.length; i++) {
            CellResult row[] = results[i];
            int max = 0;
            for (int j = 0; j < row.length; j++) {
                max = Math.max(row[j].computeRows(), max);
            }
            for (int j = 0; j < max; j++) {

                sb.setLength(0);
                for (int r = 0; r < row.length; r++) {
                    String p = row[r].getPacked(j);
                    if (!StringUtil.nullOrEmptyOrBlankString(p)) {
                        int foo = 0;
                    }
                    sb.append(StringUtil.padToLength(p, ' ', pack));
                }
                Console.println(sb.toString());
            }
            sb.setLength(0);
            for (int r = 0; r < row.length; r++) {
                sb.append(StringUtil.padToLength('=', pack));
            }
            Console.println(sb.toString());


        }
    }

    private CellResult[][] fill(CellResult[][] old, int size) {
        if (old != null && old.length >= size) {
            return old;
        }
        CellResult[][] res = new CellResult[size][size];

        // some point recoup the cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                CellResult c = new CellResult();
                c.i = i;
                c.j = j;
                res[i][j] = new CellResult();

            }
        }
        return res;
    }

    @Override
    public void unaryRule(final double prob, final String k, final String v1) {
        UnaryRule ur = new UnaryRule(k, v1, prob);
        addRule(ur);
    }

    @Override
    public void binaryRule(final double prob, final String k, final String v1, final String v2) {
        BinaryRule br = new BinaryRule(k, v1, v2, prob);
        addRule(br);
    }

    private void addRule(Rule rule) {
        String key = rule.getKey();
        ArrayList<Rule> ruleList = rules.get(key);
        if (ruleList == null) {
            ruleList = new ArrayList();
            rules.put(key, ruleList);
            if (key.length() > longestKey) {
                longestKey = key.length();
            }
        } else {
            int i = 0;
        }
        ruleList.add(rule);
    }
}





